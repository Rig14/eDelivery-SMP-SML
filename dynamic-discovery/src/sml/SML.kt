package sml

import klite.Config
import klite.info
import klite.logger
import org.xbill.DNS.DClass.IN
import org.xbill.DNS.Flags.QR
import org.xbill.DNS.Message
import org.xbill.DNS.NAPTRRecord
import org.xbill.DNS.Name
import org.xbill.DNS.Rcode.NOERROR
import org.xbill.DNS.Rcode.NOTIMP
import org.xbill.DNS.Rcode.NXDOMAIN
import org.xbill.DNS.Rcode.REFUSED
import org.xbill.DNS.Section.ANSWER
import org.xbill.DNS.Section.QUESTION
import org.xbill.DNS.Type
import org.xbill.DNS.Type.A
import org.xbill.DNS.Type.CNAME
import org.xbill.DNS.Type.NAPTR
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

class SMLServer(
  private val rootDomain: String,
  private val listen: InetSocketAddress = InetSocketAddress("0.0.0.0",Config.optional("SML_PORT", "8053").toInt())
) {
  private val log = logger()
  private val recordRegistry = RecordRegistry(rootDomain)

  fun start() {
    log.info("Starting SML server on $listen with zone $rootDomain")
    val socket = DatagramSocket(listen)
    val buffer = ByteArray(512)

    while (true) {
      val packet = DatagramPacket(buffer, buffer.size)
      socket.receive(packet)
      val query = Message(packet.data)

      val response = handle(query)
      val out = response.toWire()
      socket.send(DatagramPacket(out, out.size, packet.address, packet.port))
    }
  }

  private fun handle(query: Message): Message {
    val response = Message(query.header.id).apply {
      header.setFlag(QR.toInt())
      addRecord(query.question, QUESTION)
    }

    val name = query.question.name
    if (!name.subdomain(Name.fromString(rootDomain))) {
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
    response.addRecord(recordRegistry.lookupSmpARecord(smpHash), ANSWER)
  }

  private fun handleNaptr(query: Message, response: Message) {
    response.addRecord(
      NAPTRRecord(
        query.question.name, IN, 3600L, 100, 10, "U",
        "Meta:SMP", "!^.*$!http://smp!", Name.root
      ),
      ANSWER
    )
    response.header.rcode = NOERROR
  }

  private fun handleCname(query: Message, response: Message) {
    val smpHash = query.question.name.getLabelString(0)
    val record = recordRegistry.lookupSmpCname(smpHash)
    log.info("Found ${if (record == null) "no " else ""}record for $smpHash")
    if (record == null) {
      response.header.rcode = NXDOMAIN
    } else {
      response.addRecord(record, ANSWER)
    }
  }
}
