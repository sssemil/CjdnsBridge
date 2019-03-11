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

package sssemil.com.bridge.net.structures

import java.util.*
import kotlin.experimental.or

class DataBitStream(private val bits: BitSet, private var offset: Int, private var length: Int) {

    constructor(bytes: ByteArray, offset: Int, length: Int) : this(
        BitSet.valueOf(bytes),
        Byte.SIZE_BITS * offset,
        Byte.SIZE_BITS * length
    )

    fun takeBit() = if (length - offset - 1 < 0) throw InsufficientBitsException(offset, length, 1)
    else bits.get(offset++)

    fun takeByte(): Byte {
        var result: Byte = 0
        if (length - offset - Byte.SIZE_BITS < 0) throw InsufficientBitsException(offset, length, Byte.SIZE_BITS)
        for (i in offset until offset + Byte.SIZE_BITS) {
            if (bits.get(i)) {
                result = result or (1 shl i % Byte.SIZE_BITS).toByte()
            }
        }
        offset += Byte.SIZE_BITS
        return result
    }

    fun takeBits(n: Int): BitSet {
        val result = if (length - offset - n < 0) throw InsufficientBitsException(offset, length, n)
        else bits.get(offset, offset + n)
        offset += n
        return result
    }
}