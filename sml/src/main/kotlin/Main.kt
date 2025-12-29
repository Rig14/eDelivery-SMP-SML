package ee.rivis.sml

import org.xbill.DNS.Lookup
import org.xbill.DNS.SimpleResolver
import org.xbill.DNS.Type
import java.security.MessageDigest

fun main() {
    val resolver = SimpleResolver("127.0.0.1").apply { port=5353 }

    val lookup = Lookup("B-8bb529042b90c77892efd34f7395f504.sml.test", Type.CNAME).apply { setResolver(resolver) }
    val result = lookup.run()

    result?.forEach { println(it.rdataToString()) }

}

fun generateHashCname(identifier: String): String {
    val md = MessageDigest.getInstance("MD5")
    md.reset()
    val messageDigest = md.digest(identifier.toByteArray())
    val hex = messageDigest.joinToString("") { "%02x".format(it) }
    return "B-$hex"
}