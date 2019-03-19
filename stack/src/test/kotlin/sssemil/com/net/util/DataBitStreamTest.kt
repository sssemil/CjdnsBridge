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

package sssemil.com.net.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import sssemil.com.net.structures.DataBitStream
import sssemil.com.net.structures.InsufficientBitsException
import java.util.*

class DataBitStreamTest {

    @Test
    fun testByte() {
        val testBytes = byteArrayOf(0, 42, -100, Byte.MIN_VALUE, Byte.MAX_VALUE, 0)
        val dataBitStream = DataBitStream(testBytes, 0, testBytes.size)
        testBytes.forEach {
            assertEquals(it, dataBitStream.takeByte())
        }
    }

    @Test
    fun testByteFailInsufficient() {
        val testBytes = byteArrayOf(0, 42)
        val testBits = BitSet.valueOf(testBytes)
        val dataBitStream = DataBitStream(testBits, 0, testBytes.size * 8 - 1)
        assertEquals(testBytes[0], dataBitStream.takeByte())

        val thrown = assertThrows(
            InsufficientBitsException::class.java
        ) { dataBitStream.takeByte() }

        assertTrue(thrown.message.equals("Only 7 bits left, but required 8!"))
    }

    @Test
    fun testBit() {
        val testBitsBoolean = booleanArrayOf(false, false, true, false)
        val testBits = BitSet(testBitsBoolean.size).also {
            testBitsBoolean.forEachIndexed { i, b ->
                it[i] = b
            }
        }
        val dataBitStream = DataBitStream(testBits, 0, testBitsBoolean.size)
        testBitsBoolean.forEach {
            assertEquals(it, dataBitStream.takeBit())
        }
    }

    @Test
    fun testBits() {
        val testBitsBoolean = booleanArrayOf(false, false, true, false)
        val testBits = BitSet(testBitsBoolean.size).also {
            testBitsBoolean.forEachIndexed { i, b ->
                it[i] = b
            }
        }
        val dataBitStream = DataBitStream(testBits, 0, testBitsBoolean.size)
        assertEquals(testBits, dataBitStream.takeBits(testBitsBoolean.size))
    }
}
