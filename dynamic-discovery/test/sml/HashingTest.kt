package sml

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HashingTest {
  val recipient = "urn:oasis:names:tc:ebcore:partyid-type:unregistered:C4"

  @Test fun mustHashCnameCorrectly() = assertEquals("B-8bb529042b90c77892efd34f7395f504", getCnameHash(recipient))

  @Test fun mustHashNaptrCorrectly() = assertEquals("FSPLJSRASNOET76WWBU3VGOATHNBC7HNZ7EUJXU5LKJFNIZVJJSA", getNaptrHash(recipient))
}