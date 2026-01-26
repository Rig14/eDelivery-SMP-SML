package sml

import klite.Config
import klite.error
import klite.info
import klite.logger
import org.xbill.DNS.ARecord
import org.xbill.DNS.CNAMERecord
import org.xbill.DNS.DClass.IN
import org.xbill.DNS.Flags.QR
import org.xbill.DNS.Message
import org.xbill.DNS.Name
import org.xbill.DNS.Rcode.NOTIMP
import org.xbill.DNS.Rcode.REFUSED
import org.xbill.DNS.Section.ANSWER
import org.xbill.DNS.Section.QUESTION
import org.xbill.DNS.Type
import org.xbill.DNS.Type.A
import org.xbill.DNS.Type.CNAME
import org.xbill.DNS.Type.NAPTR
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class SMLServer(
  private val rootDomain: Name,
  private val listen: InetSocketAddress = InetSocketAddress("0.0.0.0",Config.optional("SML_PORT", "8053").toInt())
) {
  private val log = logger()

  private val cnameRecords = mutableMapOf(
    "B-8bb529042b90c77892efd34f7395f504" to CNAMERecord(
      Name("B-8bb529042b90c77892efd34f7395f504.$rootDomain"),
      IN,
      60,
      Name("smp.")
    )
  )

  private val aRecords = mutableMapOf(
    "B-8bb529042b90c77892efd34f7395f504" to ARecord(
      Name("B-8bb529042b90c77892efd34f7395f504.$rootDomain"),
      IN,
      60,
      InetAddress.getByName("172.20.0.250")
    )
  )

  fun start() {
    log.info("Starting SML server on $listen")
    val socket = DatagramSocket(listen)
    val buffer = ByteArray(512)

    while (true) {
      try {
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)

        val query = Message(packet.data)
        val response = handle(query)

        val out = response.toWire()
        socket.send(DatagramPacket(out, out.size, packet.address, packet.port))
      } catch (e: Exception) {
        log.error(e)
      }
    }
  }

  private fun handle(query: Message): Message {
    val response = Message(query.header.id).apply {
      header.setFlag(QR.toInt())
      addRecord(query.question, QUESTION)
    }

    val name = query.question.name
    if (!name.subdomain(rootDomain)) {
      response.header.rcode = REFUSED
      return response
    }

    val type = query.question.type
    log.info("Handling query for ${Type.string(type)} $name")
    when (type) {
      CNAME -> handleCname(query, response)
      NAPTR -> handleNaptr(query, response)
      A -> handleA(query, response)
      else -> response.apply { header.rcode = NOTIMP }
    }

    return response
  }

  private fun handleA(query: Message, response: Message) {
    val smpHash = query.question.name.getLabelString(0)
    response.addRecord(aRecords[smpHash], ANSWER)
  }

  private fun handleNaptr(query: Message, response: Message) {
    response.apply { header.rcode = NOTIMP } // TODO impl
  }

  private fun handleCname(query: Message, response: Message) {
    val smpHash = query.question.name.getLabelString(0)
    val records = cnameRecords[smpHash]
    log.info("Found ${if (records == null) "no " else ""}record for $smpHash")
    response.addRecord(records, ANSWER)
  }
}
