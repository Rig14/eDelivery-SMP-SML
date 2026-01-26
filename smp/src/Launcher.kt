import klite.Config
import klite.Server
import klite.annotations.annotated
import org.xbill.DNS.Name
import sml.SMLServer
import smp.SMPRoutes
import kotlin.concurrent.thread

fun main() {
  Server().apply {
    context("/") {
      annotated<SMPRoutes>()
    }

    thread {
      SMLServer(Name.fromString(Config.optional("SML_ZONE", "testsml."))).start()
    }

    start()
  }
}