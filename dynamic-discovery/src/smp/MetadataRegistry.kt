package smp

import java.net.URI

class MetadataRegistry(serviceMetadataGenerator: ServiceMetadataGenerator) {
  private val metadata = mapOf(
    "urn:oasis:names:tc:ebcore:partyid-type:unregistered::receiver" to serviceMetadataGenerator.generateMetadata(
      "receiver",
      URI("http://ap-receiver:8080/services/msh"),
      "-----BEGIN CERTIFICATE-----MIIDBzCCAe+gAwIBAgIUbuRbzUbZmORNjZar6OrGHZf3f54wDQYJKoZIhvcNAQELBQAwEzERMA8GA1UEAwwIcmVjZWl2ZXIwHhcNMjYwMTIzMTM1NzI1WhcNMzEwMTIyMTM1NzI1WjATMREwDwYDVQQDDAhyZWNlaXZlcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMl4UeYRkI9eC958NgYsBufFFtgg8WjL+DeFSUkCfY+sK78yo0fQygxrVbktw4wJVLJ/aj8vLt/fDFXYdoGNhv5pVT1qlUyGCG0+jVK66Z+WjR3PuLAZ24XPDacTqCe1tbmzRItmUwQWIV+HIXxChGYu0/S7Cz4xgIP8QfczO0FBrZIS88+Ku1LGrv6hRjAlrvz2RVL7mk1rhxdLeNy3aKVhXeifA/sQo3YDdGPtvNCO+ICjxIuROhj3YgZ9Y1Dl8H978j6GHtZGFK4k02ol45MklgsRD7mKgvZraS25ZwTpSWMp+dw2pdU6L5Gb7Vr+wWeGQ1jb/97TshqVx7Te230CAwEAAaNTMFEwHQYDVR0OBBYEFOSf7RTt28wd1GBaOqcYdgnWK7AqMB8GA1UdIwQYMBaAFOSf7RTt28wd1GBaOqcYdgnWK7AqMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAF3WWPoK8BghNw6Ihai6aX2xv2xrF+Vo+XT6ErEg0jnQVk3LGFlSbNaAjevhPp/8+VUROqrf52zto219+rwkjbPs7/ApTS/UabmUjys9vv43Mla2aLq/iqlk4QZQf6g/dmFOrzTnFosvLrruWUBCT8g7Er8hqYHwmitSUOiLupAIDSLqGtffxQKnao7M5PxP0ihXyoXVzlQ7kSqqydYp3WZFsfR9GIkgmRk5tGb3bR18EMdcPKDhZFXJrNEG6Xz4fmY+zT1stpsdhvUMADvh5j4+76vCHSf/EjAWjz4A+n6S6u4QR/s03EHb8jhGEJxr429LzAowRgiz/dveLmPxu3A=-----END CERTIFICATE-----"
    )
  )

  fun metadataFor(partyId: String) = metadata[partyId]
}