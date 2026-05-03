package sml

import org.xbill.DNS.*
import org.xbill.DNS.DClass.IN
import java.net.InetAddress
import java.security.MessageDigest

class RecordRegistry(private val rootDomain: String) {
  private val cnameRecords = mapOf(
    "urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4" to "smp.",
  ).map { getCnameHash(it.key) to it.value }.toMap()

  private val aRecords = mapOf(
    "urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4" to "172.20.0.250",
  ).map { getCnameHash(it.key) to it.value }.toMap()

  fun lookupSmpCname(hash: String): CNAMERecord? {
    val name = cnameRecords[hash] ?: return null
    return CNAMERecord(Name("$hash.$rootDomain"), IN, 60, Name(name))
  }

  fun lookupSmpARecord(hash: String): ARecord? {
    val address = aRecords[hash] ?: return null
    return ARecord(Name("$hash.$rootDomain"), IN, 60, InetAddress.getByName(address))
  }

  private fun getCnameHash(recipient: String): String {
    val hash = MessageDigest
      .getInstance("MD5")
      .digest(recipient.lowercase().toByteArray())
      .toHexString()

    return "B-$hash"
  }
}
