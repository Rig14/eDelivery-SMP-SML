package sml

import app.hexToBase32
import java.security.MessageDigest

fun getCnameHash(recipient: String): String {
  val hash = MessageDigest
    .getInstance("MD5")
    .digest(recipient.lowercase().toByteArray())
    .toHexString()

  return "B-$hash"
}

fun getNaptrHash(recipient: String): String {
  return MessageDigest
    .getInstance("SHA256")
    .digest(recipient.lowercase().toByteArray())
    .toHexString()
    .hexToBase32()
    .trimEnd('=')
}