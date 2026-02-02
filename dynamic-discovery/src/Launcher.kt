import klite.Config
import klite.Server
import klite.annotations.annotated
import sml.SMLServer
import smp.SMPRoutes
import kotlin.concurrent.thread

fun main() {
  Server().apply {
    context("/") {
      annotated<SMPRoutes>()
    }

    thread {
      SMLServer(Config.optional("SML_ZONE", "testsml.")).start()
    }

    start()
  }
}