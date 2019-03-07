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

import sssemil.com.bridge.util.toBytes
import sssemil.com.net.layers.network.exceptions.EmptyPacketException
import sssemil.com.net.layers.network.exceptions.InvalidTypeException
import sssemil.com.net.util.number.Octet
import sssemil.com.net.util.number.toOctet
import java.io.DataInputStream
import java.net.Inet6Address
import java.util.*

data class Ipv6Packet(
    val version: Octet = IPV6,
    val trafficClass: Byte = 0,
    val flowLabel: Array<Octet> = Array(5) { 0.toOctet() },
    var payloadLength: Short = 0,
    val nextHeader: Byte = UDP,
    val hopLimit: Byte,
    val sourceAddress: Inet6Address,
    val destinationAddress: Inet6Address,
    val extensionHeaders: Array<Ipv6ExtensionHeader> = Array(0) { Ipv6ExtensionHeader() },
    val payload: Payload? = null
) {
    fun build(): ByteArray {
        val arr = ArrayList<Byte>()

        val payloadArr = payload?.build()

        payloadLength = payloadArr?.size?.toShort() ?: 0

        var tmp = version.toInt()
        tmp = tmp.shl(8).or(trafficClass.toInt())
        tmp = tmp.shl(4).or(flowLabel[0].toInt())
        tmp = tmp.shl(4).or(flowLabel[1].toInt())
        tmp = tmp.shl(4).or(flowLabel[2].toInt())
        tmp = tmp.shl(4).or(flowLabel[3].toInt())
        tmp = tmp.shl(4).or(flowLabel[4].toInt())

        arr.addAll(tmp.toBytes().asList())

        arr.addAll(payloadLength.toBytes().asList())
        arr.add(nextHeader)
        arr.add(hopLimit)
        arr.addAll(sourceAddress.address.asList())
        arr.addAll(destinationAddress.address.asList())
        extensionHeaders.forEach {
            arr.addAll(it.build().asList())
        }
        payloadArr?.asList()?.let { arr.addAll(it) }

        return arr.toByteArray()
    }

    companion object {
        fun parse(arr: ByteArray): Ipv6Packet = with(DataInputStream(arr.inputStream())) {
            if (arr.isEmpty()) {
                throw EmptyPacketException("Can't process an empty packet!")
            }

            val version = read().shr(4).toOctet()

            if (version != IPV6) {
                throw InvalidTypeException("Can't parse not IPv6 packets!")
            }

            read(ByteArray(3)) // skip 3 bytes

            val payloadLength = readShort()
            val nextHeader = read().toByte()
            val hopLimit = read().toByte()

            val sourceAddress = ByteArray(16)
            read(sourceAddress)

            val destinationAddress = ByteArray(16)
            read(destinationAddress)

            val extensionHeaders = ArrayList<Ipv6ExtensionHeader>()

            var nextHeaderTmp = nextHeader
            while (nextHeaderTmp == HOPOPT
                || nextHeaderTmp == IPV6_ROUTE
                || nextHeaderTmp == IPV6_OPTS
            ) {
                val e = Ipv6ExtensionHeader.parse(this)
                nextHeaderTmp = e.nextHeader
                extensionHeaders.add(e)
            }

            val payload = if (nextHeaderTmp == IPV6_ICMP) {
                Icmp6Packet.parse(this)
            } else UnknownPayload.parse(this)

            return Ipv6Packet(
                version = version,
                payloadLength = payloadLength,
                nextHeader = nextHeader,
                hopLimit = hopLimit,
                sourceAddress = Inet6Address.getByAddress(sourceAddress) as Inet6Address,
                destinationAddress = Inet6Address.getByAddress(destinationAddress) as Inet6Address,
                extensionHeaders = extensionHeaders.toArray(Array(extensionHeaders.size) { Ipv6ExtensionHeader() }),
                payload = payload
            )
        }

        val IPV6: Octet = 6.toOctet()

        const val UDP = 0x11.toByte()
        const val IPV6_ICMP = 0x3A.toByte()

        //skip these
        const val IPV6_OPTS = 0x3C.toByte()
        const val HOPOPT = 0.toByte()
        const val IPV6_ROUTE = 0x2B.toByte()
    }
}