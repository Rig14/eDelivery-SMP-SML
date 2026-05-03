package app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class Extensions {
  @Test fun `empty string encodes to empty string`() = assertEquals("", "".hexToBase32())
  @Test fun `single byte`() = assertEquals("ME======", "61".hexToBase32())
  @Test fun `hash encodes correctly`() = assertEquals("FSPLJSRASNOET76WWBU3VGOATHNBC7HNZ7EUJXU5LKJFNIZVJJSA====", "2c9eb4ca20935c49ffd6b069ba99c099da117cedcfc944de9d5a9256a3354a64".hexToBase32())
  @Test fun `all zeros`() = assertEquals("AAAAAAA=", "00000000".hexToBase32())
  @Test fun `all ff bytes`() = assertEquals("777777Y=", "ffffffff".hexToBase32())
  @Test fun `padding is multiple of 8`() = assertTrue("010203".hexToBase32().length % 8 == 0)
  @Test fun `only padding chars after content`() = assertTrue("48656c6c6f".hexToBase32().trimEnd('=').none { it == '=' })
  @Test fun `output only contains valid base32 chars`() = assertTrue("deadbeef".hexToBase32().all { it in "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=" })
  @Test fun `uppercase hex input`() = assertEquals("deadbeef".hexToBase32(), "DEADBEEF".hexToBase32())
}