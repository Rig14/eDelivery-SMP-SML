package plugin

import org.xbill.DNS.DClass.IN
import org.xbill.DNS.Message
import org.xbill.DNS.Name
import org.xbill.DNS.Record
import org.xbill.DNS.SimpleResolver
import org.xbill.DNS.Type.CNAME

fun main() {
  val resolver = SimpleResolver("0.0.0.0").apply { port = 8053 }
  val query: Message = Message.newQuery(
    Record.newRecord(
      Name.fromString("B-8bb529042b90c77892efd34f7395f504.testsml."),
      CNAME,
      IN
    )
  )
  val response = resolver.send(query)
}