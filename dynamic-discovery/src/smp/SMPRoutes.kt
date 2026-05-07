package smp

import klite.HttpExchange
import klite.NotFoundException
import klite.StatusCode.Companion.OK
import klite.annotations.GET
import klite.annotations.PathParam

class SMPRoutes(private val metadataRegistry: MetadataRegistry) {
  @GET fun info() = "Service Discovery is running"

  @GET(":partyId/services/:serviceId")
  fun metadata(@PathParam partyId: String, @PathParam serviceId: String, e: HttpExchange) {
    if (serviceId != "::eftiGateAction") throw NotFoundException("Service ID $serviceId not found")

    val metadata = metadataRegistry.metadataFor(partyId) ?: throw NotFoundException("Metadata for service $serviceId not found")
    e.send(OK, metadata, "application/xml")
  }
}