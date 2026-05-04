package sml

import org.xbill.DNS.*
import org.xbill.DNS.DClass.IN

val SCHEME_ID = "urn:oasis:names:tc:ebcore:partyid-type:unregistered"

class RecordRegistry(private val rootDomain: String) {
  private val cnameRecords = mapOf(
    "$SCHEME_ID:C4" to "smp.",
    "$SCHEME_ID:receiver" to "smp.",
  ).map { getCnameHash(it.key) to it.value }.toMap()

  private val naptrRecords = mapOf(
    "$SCHEME_ID:C4" to "!^.*$!http://smp!",
    "$SCHEME_ID:receiver" to "!^.*$!http://smp!",
  ).map { getNaptrHash(it.key) to it.value }.toMap()


  fun lookupSmpCname(hash: String): CNAMERecord? {
    val name = cnameRecords[hash] ?: return null
    return CNAMERecord(Name("$hash.$rootDomain"), IN, 60, Name(name))
  }

  fun lookupSmpNaptr(hash: String): NAPTRRecord? {
    val regexp = naptrRecords[hash] ?: return null
    return NAPTRRecord(Name("$hash.$rootDomain"), IN, 60, 100, 10, "U",
      "Meta:SMP", regexp, Name.root
    )
  }
}
