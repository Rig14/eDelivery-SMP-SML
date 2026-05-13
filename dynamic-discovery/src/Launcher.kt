import klite.Config
import klite.Server
import klite.annotations.annotated
import sml.SMLServer
import smp.SMPRoutes
import java.util.concurrent.Executors
import kotlin.concurrent.thread

fun main() {
  Server(
    workerPool = Executors.newVirtualThreadPerTaskExecutor()
  ).apply {
    context("/") {
      annotated<SMPRoutes>()
    }

    thread {
      SMLServer(Config.optional("SML_ZONE", "testsml.")).start()
    }

    start()
  }
}