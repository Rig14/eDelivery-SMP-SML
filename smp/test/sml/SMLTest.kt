package sml

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.xbill.DNS.DClass.IN
import org.xbill.DNS.Message
import org.xbill.DNS.Name
import org.xbill.DNS.Rcode.NOTIMP
import org.xbill.DNS.Rcode.REFUSED
import org.xbill.DNS.Record
import org.xbill.DNS.SimpleResolver
import org.xbill.DNS.Type.CAA
import org.xbill.DNS.Type.CNAME
import kotlin.concurrent.thread
import kotlin.test.assertEquals

class SMLTest {
  val resolver = SimpleResolver("localhost").apply { port = 8053 }

  companion object {
    @BeforeAll
    @JvmStatic
    fun startServer() {
      thread(isDaemon = true) {
        SMLServer(Name.fromString("example.sml.")).start()
      }
      Thread.sleep(200)
    }
  }

  @Test fun queryWrongRootDomainError() {
    val query: Message = Message.newQuery(
      Record.newRecord(
        Name.fromString("example.com."),
        CNAME,
        IN
      )
    )
    val response = resolver.send(query)

    assertEquals(REFUSED, response.header.rcode)
  }

  @Test fun queryNotSupportedRecordTypeError() {
    val query: Message = Message.newQuery(
      Record.newRecord(
        Name.fromString("example.sml."),
        CAA,
        IN
      )
    )
    val response = resolver.send(query)

    assertEquals(NOTIMP, response.header.rcode)
  }

  @Test fun querySmp1() {
    val query: Message = Message.newQuery(
      Record.newRecord(
        Name.fromString("smp1.example.sml."),
        CNAME,
        IN
      )
    )
    val response = resolver.send(query)
  }
}