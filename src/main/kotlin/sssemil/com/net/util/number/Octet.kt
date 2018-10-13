/*
 * Copyright 2018 Emil Suleymanov
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

package sssemil.com.net.util.number

class Octet(byte: Byte) : Number() {

    private var value: Byte = 0

    init {
        value = byte.toInt().shl(4).shr(4).toByte()
    }

    override fun toByte() = value

    override fun toChar() = value.toChar()

    override fun toDouble() = value.toDouble()

    override fun toFloat() = value.toFloat()

    override fun toInt() = value.toInt()

    override fun toLong() = value.toLong()

    override fun toShort() = value.toShort()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Octet

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.toInt()
    }

    override fun toString(): String {
        return value.toString()
    }
}

fun Number.toOctet() = Octet(toByte())