package smp

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ServiceMetadataGeneratorTest {
  val metadataGenerator = ServiceMetadataGenerator()

  @Test fun receiverCertificate() {
    val cert = metadataGenerator.receiverCertificate("certs/own.crt")

    assertTrue(!cert.contains("-----BEGIN CERTIFICATE-----") && !cert.contains("-----END CERTIFICATE-----"), "Certificate should not contain BEGIN/END delimiters")
    assertTrue(!cert.contains("\n"), "Certificate should not contain new line chars")
  }
}