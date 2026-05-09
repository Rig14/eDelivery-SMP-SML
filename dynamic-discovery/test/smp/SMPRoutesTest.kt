package smp

import io.mockk.mockk
import klite.HttpExchange
import klite.NotFoundException
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SMPRoutesTest {
  private val knownPartyId = "urn:oasis:names:tc:ebcore:partyid-type:unregistered::receiver"
  private val missingPartyId = "urn:oasis:names:tc:ebcore:partyid-type:unregistered::missing"
  private val supportedServiceId = "::eftiGateAction"
  private val routes = SMPRoutes(MetadataRegistry(ServiceMetadataGenerator()))

  @Test
  fun `info returns a health message`() {
    assertEquals("Service Discovery is running", routes.info())
  }

  @Test
  fun `metadata rejects unknown service id`() {
    val unknownServiceId = "::unknown"
    val exception = assertFailsWith<NotFoundException> {
      routes.metadata(knownPartyId, unknownServiceId, mockk())
    }

    assertEquals("Service ID $unknownServiceId not found", exception.message)
  }

  @Test
  fun `metadata rejects unknown party id`() {
    val exception = assertFailsWith<NotFoundException> {
      routes.metadata(missingPartyId, supportedServiceId, mockk())
    }

    assertEquals("Metadata for service $supportedServiceId not found", exception.message)
  }
}
