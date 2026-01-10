package smp

import org.intellij.lang.annotations.Language
import java.io.File
import java.net.URI
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64


class ServiceMetadataGenerator {
  private val receiverUri = URI("http://ap-receiver:8080/services/msh")

  internal fun loadFile(path: String) = File(path).readText()

  fun generateMetadata(party: String, service: String): String {
    val cert = loadFile("certs/own.crt")
    val metadata = serviceMetadata(party, cert)
    val signedInfo = signedInfo(metadata)

    val privateKey = loadPrivateKey(loadFile("certs/own.key"))
    val signature = signWithRsaSha256(privateKey, signedInfo.toByteArray())

    @Language("xml")
    val result = """
      <?xml version="1.0" encoding="UTF-8"?>
      <SignedServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
        $metadata
        <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
          $signedInfo
          <SignatureValue>$signature</SignatureValue>
          <KeyInfo>
            <X509Data>
              <X509SubjectName>${subjectDn(cert)}</X509SubjectName>
              <X509Certificate>${cert.renderCert()}</X509Certificate>
            </X509Data>
          </KeyInfo>
        </Signature>
      </SignedServiceMetadata>
    """.trimIndent()

    return result
  }

  @Language("xml")
  private fun signedInfo(serviceMetadata: String) = """
    <SignedInfo>
      <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315" />
      <SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256" />
      <Reference URI="">
        <Transforms>
          <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" />
        </Transforms>
        <DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256" />
        <DigestValue>${digest(serviceMetadata)}</DigestValue>
      </Reference>
    </SignedInfo>
  """.trimIndent()

  @Language("xml")
  private fun serviceMetadata(party: String, receiverCert: String) = """
    <ServiceMetadata>
      <ServiceInformation>
        <ParticipantIdentifier>$party</ParticipantIdentifier>
        <DocumentIdentifier>eftiGateAction</DocumentIdentifier>
        <ProcessList>
          <Process>
            <ProcessIdentifier>eftiGateService</ProcessIdentifier>
            <ServiceEndpointList>
              <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                <EndpointURI>$receiverUri</EndpointURI>
                <Certificate>${receiverCert.renderCert()}</Certificate>
                <ServiceDescription>This service is used to upload identifiers to the eFTI Gate</ServiceDescription>
                <TechnicalContactUrl>contact@efti.eu</TechnicalContactUrl>
              </Endpoint>
            </ServiceEndpointList>
          </Process>
        </ProcessList>
      </ServiceInformation>
    </ServiceMetadata>
  """.trimIndent()

  private fun digest(serviceMetadata: String): String {
    @Language("xml")
    val toSign = """
      <SignedServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
          $serviceMetadata
      </SignedServiceMetadata>
    """.trimIndent()

    val xmlBytes = toSign.toByteArray(Charsets.UTF_8)

    val digest = MessageDigest.getInstance("SHA-256").digest(xmlBytes)
    return Base64.getEncoder().encodeToString(digest)
  }

  fun loadPrivateKey(pem: String): PrivateKey {
    val keyBytes = Base64.getDecoder().decode(
      pem.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replace("\\s".toRegex(), "")
    )
    val keySpec = PKCS8EncodedKeySpec(keyBytes)
    return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
  }

  fun signWithRsaSha256(privateKey: PrivateKey, data: ByteArray): String {
    val signer = Signature.getInstance("SHA256withRSA")
    signer.initSign(privateKey)
    signer.update(data)
    val signatureBytes = signer.sign()
    return Base64.getEncoder().encodeToString(signatureBytes)
  }

  internal fun subjectDn(cert: String): String {
    val certContent = cert
      .replace("-----BEGIN CERTIFICATE-----", "")
      .replace("-----END CERTIFICATE-----", "")
      .replace("\\s".toRegex(), "")

    val decoded = Base64.getDecoder().decode(certContent)

    val certFactory = CertificateFactory.getInstance("X.509")
    val cert = certFactory.generateCertificate(decoded.inputStream()) as X509Certificate

    val subjectDN = cert.subjectX500Principal.name
    return subjectDN
  }

  private fun String.renderCert() = trim().removeSurrounding("-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----").trim().replace("\n", "&#xD;")
}