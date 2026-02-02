package sml

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.xbill.DNS.DClass.IN
import org.xbill.DNS.Message
import org.xbill.DNS.Message.newQuery
import org.xbill.DNS.NXTRecord
import org.xbill.DNS.Name
import org.xbill.DNS.Name.fromString
import org.xbill.DNS.Rcode.NOERROR
import org.xbill.DNS.Rcode.NXDOMAIN
import org.xbill.DNS.Rcode.REFUSED
import org.xbill.DNS.Record
import org.xbill.DNS.Record.newRecord
import org.xbill.DNS.Section
import org.xbill.DNS.Section.QUESTION
import org.xbill.DNS.SimpleResolver
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
        SMLServer("testsml.").start()
      }
      Thread.sleep(200)
    }
  }

  @Test fun responseIncludesQuestion() {
    val query = newQuery(newRecord(
        fromString("abc.testsml."),
        CNAME,
        IN
      )
    )

    val response = resolver.send(query)

    assertEquals(query.getSection(QUESTION), response.getSection(QUESTION))
    assertEquals(NXDOMAIN, response.header.rcode)
  }

  @Test fun refuseNonZone() {
    val query = newQuery(newRecord(
      fromString("abc.example."),
      CNAME,
      IN
    ))

    val response = resolver.send(query)

    assertEquals(REFUSED, response.header.rcode)
  }
}