package app

fun String.hexToBase32(): String {
  val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
  val bytes = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
  val sb = StringBuilder()
  var buffer = 0
  var bitsLeft = 0
  for (byte in bytes) {
    buffer = (buffer shl 8) or (byte.toInt() and 0xFF)
    bitsLeft += 8
    while (bitsLeft >= 5) {
      bitsLeft -= 5
      sb.append(alphabet[(buffer shr bitsLeft) and 0x1F])
    }
  }
  if (bitsLeft > 0) sb.append(alphabet[(buffer shl (5 - bitsLeft)) and 0x1F])
  while (sb.length % 8 != 0) sb.append('=')
  return sb.toString()
}