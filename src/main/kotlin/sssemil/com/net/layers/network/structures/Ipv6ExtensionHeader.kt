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

package sssemil.com.net.layers.network.structures

import java.io.DataInputStream
import java.util.*

data class Ipv6ExtensionHeader(
    val nextHeader: Byte = 0, val headerExtensionLength: Byte = 0,
    val content: ByteArray = byteArrayOf()
) {
    fun build(): ByteArray {
        val arr = ArrayList<Byte>()
        arr.add(nextHeader)
        arr.add(headerExtensionLength)
        arr.addAll(content.asList())
        return arr.toByteArray()
    }

    companion object {
        fun parse(stream: DataInputStream): Ipv6ExtensionHeader {
            val nextHeader = stream.read().toByte()
            val headerExtensionLength = stream.read().toByte()
            val content = ByteArray((1 + headerExtensionLength) * 4)
            return Ipv6ExtensionHeader(nextHeader, headerExtensionLength, content)
        }
    }
}