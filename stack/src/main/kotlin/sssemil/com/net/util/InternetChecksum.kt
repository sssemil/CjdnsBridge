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

import sssemil.com.net.packet.IPv6
import sssemil.com.net.packet.types.IpProtocol
import java.nio.ByteBuffer

object InternetChecksum {

    fun checksumHelper(
        payloadPreChecksum: ByteArray,
        payloadPostChecksum: ByteArray,
        parent: IPv6,
        protocol: IpProtocol
    ): Short {
        val totalPacketLength = payloadPreChecksum.size + 2 + payloadPostChecksum.size
        val pseudoHeaderData = ByteArray(totalPacketLength + 40)
        val pbb = ByteBuffer.wrap(pseudoHeaderData)
        pbb.put(parent.sourceAddress.address)
        pbb.put(parent.destinationAddress.address)
        pbb.putInt(totalPacketLength.toLong().toUInt().toInt())
        pbb.put(byteArrayOf(0, 0, 0))
        pbb.put(protocol.ipProtocolNumber.toByte())
        pbb.put(payloadPreChecksum)
        pbb.putShort(0)
        pbb.put(payloadPostChecksum)
        return InternetChecksum.calculateChecksum(pseudoHeaderData).toShort()
    }

    /**
     * Calculate the Internet Checksum of a buffer (RFC 1071 - http://www.faqs.org/rfcs/rfc1071.html)
     * Algorithm is
     * 1) apply a 16-bit 1's complement sum over all octets (adjacent 8-bit pairs [A,B], final odd length is [A,0])
     * 2) apply 1's complement to this final sum
     *
     *
     * Notes:
     * 1's complement is bitwise NOT of positive value.
     * Ensure that any carry bits are added back to avoid off-by-one errors
     *
     *
     * From https://stackoverflow.com/a/4114507/3119031.
     *
     * @param buf The message
     * @return The checksum
     */
    fun calculateChecksum(buf: ByteArray): Long {
        var length = buf.size
        var i = 0

        var sum: Long = 0
        var data: Long

        // Handle all pairs
        while (length > 1) {
            // Corrected to include @Andy's edits and various comments on Stack Overflow
            data = (buf[i].toInt() shl 8 and 0xFF00 or (buf[i + 1].toInt() and 0xFF)).toLong()
            sum += data
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
            if (sum and -0x10000 > 0) {
                sum = sum and 0xFFFF
                sum += 1
            }

            i += 2
            length -= 2
        }

        // Handle remaining byte in odd length buffers
        if (length > 0) {
            // Corrected to include @Andy's edits and various comments on Stack Overflow
            sum += (buf[i].toInt() shl 8 and 0xFF00).toLong()
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
            if (sum and -0x10000 > 0) {
                sum = sum and 0xFFFF
                sum += 1
            }
        }

        // Final 1's complement value correction to 16-bits
        sum = sum.inv()
        sum = sum and 0xFFFF
        return sum
    }
}