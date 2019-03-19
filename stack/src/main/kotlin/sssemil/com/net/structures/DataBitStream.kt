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

package sssemil.com.net.structures

import sssemil.com.net.util.number.Nibble
import java.util.*

class DataBitStream(private val bits: BitSet, private var offset: Int, private var length: Int) {

    constructor(bytes: ByteArray, offset: Int, length: Int) : this(
            BitSet.valueOf(bytes),
            Byte.SIZE_BITS * offset,
            Byte.SIZE_BITS * length
    )

    fun takeBit() = if (length - offset - 1 < 0) throw InsufficientBitsException(
            offset,
            length,
            1
    )
    else bits.get(offset++)

    fun takeNibble() = takePrimitive<Nibble>(Nibble.SIZE_BITS)

    fun takeByte() = takePrimitive<Byte>(Byte.SIZE_BITS)

    fun takeShort() = takePrimitive<Short>(Short.SIZE_BITS)

    fun takeInt() = takePrimitive<Int>(Int.SIZE_BITS)

    fun takeLong() = takePrimitive<Long>(Long.SIZE_BITS)

    fun takeUByte() = takePrimitive<Byte>(Byte.SIZE_BITS).toUByte()

    fun takeUShort() = takePrimitive<Short>(Short.SIZE_BITS).toUShort()

    fun takeUInt() = takePrimitive<Int>(Int.SIZE_BITS).toUInt()

    fun takeULong() = takePrimitive<Long>(Long.SIZE_BITS).toULong()

    fun takeBits(n: Int): BitSet {
        val result = if (length - offset - n < 0) throw InsufficientBitsException(
                offset,
                length,
                n
        )
        else bits.get(offset, offset + n)
        offset += n
        return result
    }

    fun takeByteArray(n: Int) = ByteArray(n) { takeByte() }

    private fun <T : Number> takePrimitive(sizeBits: Int): T {
        var result = 0
        if (length - offset - sizeBits < 0) throw InsufficientBitsException(
                offset,
                length,
                sizeBits
        )
        for (i in offset until offset + sizeBits) {
            if (bits.get(i)) {
                result = result or (1 shl i % sizeBits)
            }
        }

        offset += sizeBits
        return result as T
    }

    fun discard() {
        offset = length
    }

    fun isEmpty() = offset >= length

    fun remainingBits() = length - offset
}