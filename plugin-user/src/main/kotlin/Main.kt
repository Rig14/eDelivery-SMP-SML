package ee.rivis

import org.intellij.lang.annotations.Language
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpResponse.BodyHandlers.ofString
import java.util.UUID
import kotlin.io.encoding.Base64

val WS_ENDPOINT = URI("http://localhost:8443/services/wsplugin")
val WS_USER = "service_account"
val WS_PASSWORD = "Azerty59*1234567"
val FROM = "estonia"
val TO = "borduria"

fun main() {
    @Language("XML")
    val message = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <hello>world</hello>
    """.trimIndent()

    val request = HttpRequest.newBuilder()
        .uri(WS_ENDPOINT)
        .headers(
            "Content-Type",
            "application/soap+xml",
            "Authorization",
            "Basic ${Base64.encode("$WS_USER:$WS_PASSWORD".toByteArray())}"
        )
        .POST(ofString(generateMessage(message)))
        .build()

    val response = HttpClient.newHttpClient().send(request, ofString())
    if (response.statusCode() in (200..299)) {
        println("Message sent")
    } else {
        error("Got error code ${response.statusCode()} when sending message to Access point ${response.body()}")
    }
}

@Language("XML")
fun generateMessage(message: String): String {
    val messageId = UUID.randomUUID()
    val conversationId = UUID.randomUUID()

    return """
        <S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope" xmlns:env="http://www.w3.org/2003/05/soap-envelope">
            <S:Header>
                <ns5:Messaging xmlns:ns3="http://eu.domibus.wsplugin/" xmlns:ns5="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/" xmlns:xmime="http://www.w3.org/2005/05/xmlmime">
                    <ns5:UserMessage>
                        <ns5:MessageInfo>
                            <ns5:MessageId>${messageId}@domibus.eu</ns5:MessageId>
                        </ns5:MessageInfo>
                        <ns5:PartyInfo>
                            <ns5:From>
                                <ns5:PartyId type="urn:oasis:names:tc:ebcore:partyid-type:unregistered">$FROM</ns5:PartyId>
                                <ns5:Role>http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator</ns5:Role>
                            </ns5:From>
                            <ns5:To>
                                <ns5:PartyId type="urn:oasis:names:tc:ebcore:partyid-type:unregistered">$TO</ns5:PartyId>
                                <ns5:Role>http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder</ns5:Role>
                            </ns5:To>
                        </ns5:PartyInfo>
                        <ns5:CollaborationInfo>
                            <ns5:Service type="tc1">bdx:noprocess</ns5:Service>
                            <ns5:Action>eftiGateAction</ns5:Action>
                            <ns5:ConversationId>$conversationId</ns5:ConversationId>
                        </ns5:CollaborationInfo>
                        <ns5:MessageProperties>
                            <ns5:Property name="originalSender">urn:oasis:names:tc:ebcore:partyid-type:unregistered:C1</ns5:Property>
                            <ns5:Property name="finalRecipient">urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4</ns5:Property>
                        </ns5:MessageProperties>
                        <ns5:PayloadInfo>
                            <ns5:PartInfo href="cid:message">
                                <ns5:PartProperties>
                                    <ns5:Property name="MimeType">text/xml</ns5:Property>
                                </ns5:PartProperties>
                            </ns5:PartInfo>
                        </ns5:PayloadInfo>
                        <ns5:ProcessingType>PUSH</ns5:ProcessingType>
                    </ns5:UserMessage>
                </ns5:Messaging>
            </S:Header>
            <S:Body>
                <ns3:submitRequest xmlns:ns3="http://eu.domibus.wsplugin/" xmlns:ns5="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/" xmlns:xmime="http://www.w3.org/2005/05/xmlmime">
                    <payload contentType="text/xml" payloadId="cid:message">
                        <value>${Base64.encode(message.toByteArray())}</value>
                    </payload>
                </ns3:submitRequest>
            </S:Body>
        </S:Envelope>
    """.trimIndent()
}