package smp

import klite.annotations.GET
import klite.annotations.PathParam
import klite.info
import klite.logger

class SMPRoutes {
  private val log = logger()
  @GET() fun info() = "Service Discovery is running"

  @GET(":partyId/services/:serviceId")
  fun metadata(@PathParam partyId: String, @PathParam serviceId: String) {
    log.info("got $partyId for $serviceId")
  }
}