import klite.Server
import klite.annotations.annotated
import smp.SMPRoutes

fun main() {
  Server().apply {
    context("/") {
      annotated<SMPRoutes>()
    }
    start()
  }
}