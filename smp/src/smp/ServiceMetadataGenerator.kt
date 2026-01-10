package smp

import org.intellij.lang.annotations.Language
import java.io.File
import java.net.URI


class ServiceMetadataGenerator {
  private val receiverUri = URI("http://ap-receiver:8080/services/msh")

  internal fun receiverCertificate(path: String) = File(path).readText().renderCert()

  fun generateMetadata(party: String, service: String): String {
    @Language("xml")
    val result = """
      <?xml version="1.0" encoding="UTF-8"?>
      <SignedServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
        <ServiceMetadata>
          <ServiceInformation>
            <ParticipantIdentifier>$party</ParticipantIdentifier>
            <DocumentIdentifier>eftiGateAction</DocumentIdentifier>
            <ProcessList>
              <Process>
                <ProcessIdentifier>eftiGateService</ProcessIdentifier>
                <ServiceEndpointList>
                  <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                    <EndpointURI>$receiverUri</EndpointURI>
                    <Certificate>${receiverCertificate("certs/own.crt")}</Certificate>
                    <ServiceDescription>This service is used to upload identifiers to the gate</ServiceDescription>
                    <TechnicalContactUrl>contact@efti.eu</TechnicalContactUrl>
                  </Endpoint>
                </ServiceEndpointList>
              </Process>
            </ProcessList>
          </ServiceInformation>
        </ServiceMetadata>
        <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
          <SignedInfo>
            <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315" />
            <SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256" />
            <Reference URI="">
              <Transforms>
                <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" />
              </Transforms>
              <DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256" />
              <DigestValue>+B+yDXNbHEcboJTL52sbg2gmZHu+gPd8JO7SnLmHHF0=</DigestValue>
            </Reference>
          </SignedInfo>
          <SignatureValue>S8vdN2yINd8UOSVj5uqClDG8qNq0cFNveDehIEWi1W85gkMxhnRRv41yX7dLSP5swLTw7nPxMpl/&#xD;
                        aT6xwTNucJgJ0wgMDPtkI3/QCl71Hr8mR63TRRRmFs7D7BxxrtTSoYhXj7lWn0UsK0kc2h70S1VQ&#xD;
                        1/GDNL/T0E86XbtaIIbhvC1RFyzZj7vhJVj84cYE7N9HxQ1rriUUopOJj9AsAuh9l5nA44UTlPAm&#xD;
                        UMYGyw6k8R7bQOiTbUCnUi3iILa8axBO+RX7IBd2eJz/grwxGA0ysNpKr82BKP6C0+ACiQ8RWJOg&#xD;
                        ts09srub3wDRvA7PFMSZ538GCDeYouoUNd9pEw==</SignatureValue>
          <KeyInfo>
            <X509Data>
              <X509SubjectName>CN=demo-smp-signing-key,OU=edelivery,O=digit,C=eu</X509SubjectName>
              <X509Certificate>MIIDHDCCAgSgAwIBAgIEZEP5vDANBgkqhkiG9w0BAQsFADBQMQswCQYDVQQGEwJldTEOMAwGA1UE&#xD;
                                CgwFZGlnaXQxEjAQBgNVBAsMCWVkZWxpdmVyeTEdMBsGA1UEAwwUZGVtby1zbXAtc2lnbmluZy1r&#xD;
                                ZXkwHhcNMjMwNDIyMTUxNDA0WhcNMzMwNDIyMTUxNDA0WjBQMQswCQYDVQQGEwJldTEOMAwGA1UE&#xD;
                                CgwFZGlnaXQxEjAQBgNVBAsMCWVkZWxpdmVyeTEdMBsGA1UEAwwUZGVtby1zbXAtc2lnbmluZy1r&#xD;
                                ZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCx993g8SAMON0uvs5wO6ihlSPrwvJR&#xD;
                                fvE6SgNKFs6vfpxZlLOzakTIoycf9BXJ9Fd9CrjD2diswAlAYVmBHWK3KTOdUuPfE7dX4aMJZNqT&#xD;
                                BdpoS6ooWj4GPBeTJ3c67FGWw7f3mGFU0sEtGkn1DNjSHddnueC0Y+HcScz7IqNiNoCi95d03qmx&#xD;
                                IPElKQifJsJrOWNEcL725vg4oDPx3p7RD0rnyPvkOdYhDxQAZBWLLsKnWmsSmgHfXJT26y1dxylU&#xD;
                                kM/u9LeDITVNM1XMkFNnSfb5EyvMtjYemjlYp/l2D7x0qD4cu7tls0ffT3cvaO2DhZpug76+l4vj&#xD;
                                C1qvjvjLAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAKl1Aqvbz9Miv0OYdVoEAGV0XAlrlnQvf+c3&#xD;
                                yyW7fPWwkWQ1sQyrHL53hKm9xYzSULYW7TGxC8dKwik9AZpvOeUMxptohTT39Hu2XSuBarEEk5HB&#xD;
                                amnCiAETBMYEgmj2MoVns916icVc03Zbqo4EEBtoED8uSCVUL+UjOEax8vgaXEgz+zcm3MR3xxxe&#xD;
                                k2RMBmdMsr0lQSbe6oXzT+IuVVVpU6feUXyEVEZwuxHJahFHblIE9vynMcHtrUTSnyyfgEGeaw5n&#xD;
                                AHUq2CgGJIrMcQNcPbmB+c3ROtEZgeWmE4xrmtDKLEOyUwxfcHzWTbG99x98IeM54S8pcoToHG+K&#xD;
                                2Ok=</X509Certificate>
            </X509Data>
          </KeyInfo>
        </Signature>
      </SignedServiceMetadata>
    """.trimIndent()

    return result
  }

  private fun String.renderCert() = trim().removeSurrounding("-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----").trim().replace("\n", "&#xD;")
}