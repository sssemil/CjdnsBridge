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

import sssemil.com.bridge.util.toBytes
import java.io.DataInputStream
import java.util.*

data class Icmp6Packet(
    val type: Byte = ECHO_REQUEST,
    val code: Byte = 0,
    var checksum: Short = 0,
    val identifier: Short = 0,
    val sequenceNumber: Short = 0
) : Payload() {

    override fun build(): ByteArray {
        val arr = ArrayList<Byte>()

        arr.add(type)
        arr.add(code)
        arr.addAll(checksum.toBytes().asList())
        arr.addAll(identifier.toBytes().asList())
        arr.addAll(sequenceNumber.toBytes().asList())

        return arr.toByteArray()
    }

    companion object {
        fun parse(stream: DataInputStream): Icmp6Packet = with(stream) {
            val type = read().toByte()
            val code = read().toByte()
            val checksum = readShort()
            val identifier = readShort()
            val sequenceNumber = readShort()

            return@with Icmp6Packet(
                type = type,
                code = code,
                checksum = checksum,
                identifier = identifier,
                sequenceNumber = sequenceNumber
            )
        }

        const val DESTINATION_UNREACHABLE: Byte = 1.toByte()
        const val PACKET_TOO_BIG: Byte = 2.toByte()
        const val TIME_EXCEEDED: Byte = 3.toByte()
        const val PARAMETER_PROBLEM: Byte = 4.toByte()
        const val ECHO_REQUEST: Byte = 128.toByte()
        const val ECHO_REPLY: Byte = 129.toByte()
    }
}