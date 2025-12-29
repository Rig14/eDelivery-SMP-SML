package ee.rivis.sml

import java.security.MessageDigest

fun main() {
    getSearchHashCname("urn:oasis:names:tc:ebcore:partyid-type:unregistered:c4")
}

fun getSearchHashCname(identifier: String) {
    val md = MessageDigest.getInstance("MD5")
    md.reset()
    val messageDigest = md.digest(identifier.toByteArray())
    val hex = messageDigest.joinToString("") { "%02x".format(it) }
}