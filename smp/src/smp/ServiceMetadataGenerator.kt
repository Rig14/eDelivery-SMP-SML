package smp

import klite.base64Encode
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
  private val receiverCert = "-----BEGIN CERTIFICATE-----MIIDBzCCAe+gAwIBAgIUbuRbzUbZmORNjZar6OrGHZf3f54wDQYJKoZIhvcNAQELBQAwEzERMA8GA1UEAwwIcmVjZWl2ZXIwHhcNMjYwMTIzMTM1NzI1WhcNMzEwMTIyMTM1NzI1WjATMREwDwYDVQQDDAhyZWNlaXZlcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMl4UeYRkI9eC958NgYsBufFFtgg8WjL+DeFSUkCfY+sK78yo0fQygxrVbktw4wJVLJ/aj8vLt/fDFXYdoGNhv5pVT1qlUyGCG0+jVK66Z+WjR3PuLAZ24XPDacTqCe1tbmzRItmUwQWIV+HIXxChGYu0/S7Cz4xgIP8QfczO0FBrZIS88+Ku1LGrv6hRjAlrvz2RVL7mk1rhxdLeNy3aKVhXeifA/sQo3YDdGPtvNCO+ICjxIuROhj3YgZ9Y1Dl8H978j6GHtZGFK4k02ol45MklgsRD7mKgvZraS25ZwTpSWMp+dw2pdU6L5Gb7Vr+wWeGQ1jb/97TshqVx7Te230CAwEAAaNTMFEwHQYDVR0OBBYEFOSf7RTt28wd1GBaOqcYdgnWK7AqMB8GA1UdIwQYMBaAFOSf7RTt28wd1GBaOqcYdgnWK7AqMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAF3WWPoK8BghNw6Ihai6aX2xv2xrF+Vo+XT6ErEg0jnQVk3LGFlSbNaAjevhPp/8+VUROqrf52zto219+rwkjbPs7/ApTS/UabmUjys9vv43Mla2aLq/iqlk4QZQf6g/dmFOrzTnFosvLrruWUBCT8g7Er8hqYHwmitSUOiLupAIDSLqGtffxQKnao7M5PxP0ihXyoXVzlQ7kSqqydYp3WZFsfR9GIkgmRk5tGb3bR18EMdcPKDhZFXJrNEG6Xz4fmY+zT1stpsdhvUMADvh5j4+76vCHSf/EjAWjz4A+n6S6u4QR/s03EHb8jhGEJxr429LzAowRgiz/dveLmPxu3A=-----END CERTIFICATE-----"

  internal fun loadFile(path: String) = File(path).readText()

  fun generateMetadata(party: String, service: String): String {
    val ownCert = loadFile("certs/keystore.crt")
    val metadata = serviceMetadata(party, receiverCert)
    val signedInfo = signedInfo(metadata)

    val privateKey = loadPrivateKey(loadFile("certs/keystore.key"))
    val signature = signWithRsaSha256(privateKey, signedInfo.toByteArray())

    @Language("xml")
    val result = """<?xml version="1.0" encoding="UTF-8"?>
      <SignedServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
        $metadata
        <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
          $signedInfo
          <SignatureValue>$signature</SignatureValue>
          <KeyInfo>
            <X509Data>
              <X509SubjectName>${subjectDn(ownCert)}</X509SubjectName>
              <X509Certificate>${ownCert.renderCert()}</X509Certificate>
            </X509Data>
          </KeyInfo>
        </Signature>
      </SignedServiceMetadata>
    """.canonicalXml()

    return result
  }

  @Language("xml")
  private fun signedInfo(serviceMetadata: String) = """
    <SignedInfo xmlns="http://www.w3.org/2000/09/xmldsig#">
      <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"></CanonicalizationMethod>
      <SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"></SignatureMethod>
      <Reference URI="">
        <Transforms>
          <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"></Transform>
        </Transforms>
        <DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"></DigestMethod>
        <DigestValue>${digest(serviceMetadata)}</DigestValue>
      </Reference>
    </SignedInfo>
  """.canonicalXml()

  @Language("xml")
  private fun serviceMetadata(party: String, receiverCert: String) = """
    <ServiceMetadata>
      <ServiceInformation>
        <ParticipantIdentifier>$party</ParticipantIdentifier>
        <DocumentIdentifier>eftiGateAction</DocumentIdentifier>
        <ProcessList>
          <Process>
            <ProcessIdentifier scheme="tc1">bdx:noprocess</ProcessIdentifier>
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
  """.canonicalXml()

  private fun digest(serviceMetadata: String): String {
    @Language("xml")
    val toSign = """
      <SignedServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
          $serviceMetadata
      </SignedServiceMetadata>
    """.canonicalXml()

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
    return signer.sign().base64Encode()
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

  private fun String.renderCert() = trim().removeSurrounding("-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----").trim().replace("\n", "")
  private fun String.canonicalXml(): String {
    val s = trim()
    return Regex("\\s+").replace(s) { m ->
      val start = m.range.first
      val end = m.range.last
      val before = if (start - 1 >= 0) s[start - 1] else null
      val after = if (end + 1 < s.length) s[end + 1] else null
      if (before == '>' || after == '<') "" else " "
    }
  }
}