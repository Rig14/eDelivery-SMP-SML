package smp

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MetadataRegistryTest {
  private val knownPartyId = "urn:oasis:names:tc:ebcore:partyid-type:unregistered::receiver"
  private val missingPartyId = "urn:oasis:names:tc:ebcore:partyid-type:unregistered::missing"
  private val registry = MetadataRegistry(ServiceMetadataGenerator())

  @Test
  fun `returns metadata for known participant`() {
    val result = registry.metadataFor(knownPartyId)

    assertNotNull(result)
    assertTrue(result.contains("<ParticipantIdentifier>receiver</ParticipantIdentifier>"))
    assertTrue(result.contains("<EndpointURI>http://ap-receiver:8080/services/msh</EndpointURI>"))
  }

  @Test
  fun `returns null for unknown participant`() {
    val result = registry.metadataFor(missingPartyId)

    assertNull(result)
  }
}
