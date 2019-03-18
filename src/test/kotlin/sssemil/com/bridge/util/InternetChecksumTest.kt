/*
 * Copyright 2019 Emil Suleymanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sssemil.com.bridge.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/*
 * From https://stackoverflow.com/a/4114507/3119031.
 */
class InternetChecksumTest {

    @Test
    fun simplestValidValue() {
        val buf = ByteArray(1) // should work for any-length array of zeros
        val expected: Long = 0xFFFF

        val actual = InternetChecksum.calculateChecksum(buf)

        assertEquals(expected, actual)
    }

    @Test
    fun validSingleByteExtreme() {
        val buf = byteArrayOf(0xFF.toByte())
        val expected: Long = 0xFF

        val actual = InternetChecksum.calculateChecksum(buf)

        assertEquals(expected, actual)
    }

    @Test
    fun validMultiByteExtrema() {
        val buf = byteArrayOf(0x00.toByte(), 0xFF.toByte())
        val expected: Long = 0xFF00

        val actual = InternetChecksum.calculateChecksum(buf)

        assertEquals(expected, actual)
    }

    @Test
    fun validExampleMessage() {
        // Berkley example http://www.cs.berkeley.edu/~kfall/EE122/lec06/tsld023.htm
        // e3 4f 23 96 44 27 99 f3
        val buf = byteArrayOf(
            0xe3.toByte(),
            0x4f.toByte(),
            0x23.toByte(),
            0x96.toByte(),
            0x44.toByte(),
            0x27.toByte(),
            0x99.toByte(),
            0xf3.toByte()
        )

        val expected: Long = 0x1aff

        val actual = InternetChecksum.calculateChecksum(buf)

        assertEquals(expected, actual)
    }

    @Test
    fun validExampleEvenMessageWithCarryFromRFC1071() {
        // RFC1071 example http://www.ietf.org/rfc/rfc1071.txt
        // 00 01 f2 03 f4 f5 f6 f7
        val buf = byteArrayOf(
            0x00.toByte(),
            0x01.toByte(),
            0xf2.toByte(),
            0x03.toByte(),
            0xf4.toByte(),
            0xf5.toByte(),
            0xf6.toByte(),
            0xf7.toByte()
        )

        val expected: Long = 0x220d

        val actual = InternetChecksum.calculateChecksum(buf)

        assertEquals(expected, actual)

    }
}