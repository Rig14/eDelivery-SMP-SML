package smp

import org.junit.jupiter.api.Test
import java.net.URI
import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServiceMetadataGeneratorTest {
  private val certPath = "certs/keystore.crt"
  private val keyPath = "certs/keystore.key"
  private val metadataGenerator = ServiceMetadataGenerator()

  @Test
  fun `subject dn is parsed from certificate`() {
    val cert = metadataGenerator.loadFile(certPath)

    val subjectDn = metadataGenerator.subjectDn(cert)

    assertEquals("CN=smp", subjectDn)
  }

  @Test
  fun `private key is parsed from PKCS#8 RSA PEM`() {
    val privateKey = metadataGenerator.loadPrivateKey(metadataGenerator.loadFile(keyPath))

    assertEquals("RSA", privateKey.algorithm)
    assertEquals("PKCS#8", privateKey.format)
  }

  @Test
  fun `rsa sha256 signature can be verified with certificate public key`() {
    val data = "metadata-to-sign".toByteArray()
    val privateKey = metadataGenerator.loadPrivateKey(metadataGenerator.loadFile(keyPath))
    val certPem = metadataGenerator.loadFile(certPath)
    val certificate = parseCertificate(certPem)

    val signature = metadataGenerator.signWithRsaSha256(privateKey, data)

    assertTrue {
      Signature.getInstance("SHA256withRSA").run {
        initVerify(certificate.publicKey)
        update(data)
        verify(Base64.getDecoder().decode(signature))
      }
    }
  }

  @Test
  fun `generated metadata includes signed envelope and receiver endpoint`() {
    val receiverCert = metadataGenerator.loadFile(certPath)

    val metadata = metadataGenerator.generateMetadata(
      partyId = "receiver",
      receiverUri = URI("http://example.test/services/msh"),
      receiverCert = receiverCert
    )

    assertTrue(metadata.contains("<SignedServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">"))
    assertTrue(metadata.contains("<ParticipantIdentifier>receiver</ParticipantIdentifier>"))
    assertTrue(metadata.contains("<EndpointURI>http://example.test/services/msh</EndpointURI>"))
    assertTrue(metadata.contains("<X509SubjectName>CN=smp</X509SubjectName>"))
    assertTrue(metadata.contains("<SignatureValue>"))
    assertTrue(!metadata.contains("-----BEGIN CERTIFICATE-----"))
  }

  private fun parseCertificate(certPem: String): X509Certificate {
    val stripped = certPem
      .replace("-----BEGIN CERTIFICATE-----", "")
      .replace("-----END CERTIFICATE-----", "")
      .replace("\\s".toRegex(), "")

    val decoded = Base64.getDecoder().decode(stripped)
    return CertificateFactory.getInstance("X.509")
      .generateCertificate(decoded.inputStream()) as X509Certificate
  }
}
