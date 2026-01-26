package smp

import klite.HttpExchange
import klite.StatusCode.Companion.OK
import klite.annotations.GET
import klite.annotations.PathParam

class SMPRoutes(
  private val metadataGenerator: ServiceMetadataGenerator
) {
  @GET() fun info() = "Service Discovery is running"

  @GET(":partyId/services/:serviceId")
  fun metadata(@PathParam partyId: String, @PathParam serviceId: String, e: HttpExchange) {
    e.send(OK, metadataGenerator.generateMetadata(partyId, serviceId), "application/xml")
  }
}