package smp

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ServiceMetadataGeneratorTest {
  val metadataGenerator = ServiceMetadataGenerator()

  @Test fun subjectDn() {
    val cert = metadataGenerator.loadFile("certs/own.crt")
    val subjectDn = metadataGenerator.subjectDn(cert)
    assertTrue { subjectDn == "CN=smp" }
  }
}