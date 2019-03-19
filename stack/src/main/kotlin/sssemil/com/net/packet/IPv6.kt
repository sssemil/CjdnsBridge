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
package sssemil.com.net.packet

import sssemil.com.common.util.Logger
import sssemil.com.net.packet.types.IpProtocol
import java.net.Inet6Address
import java.net.UnknownHostException
import java.nio.ByteBuffer

/**
 * @author Jacob Chappell (jacob.chappell@uky.edu)
 * @edited Ryan Izard, ryan.izard@bigswitch.com, rizard@g.clemson.edu
 */
data class IPv6(
    var version: Byte = 0,
    var trafficClass: Byte = 0,
    var flowLabel: Int = 0,
    var payloadLength: Short = 0,
    var nextHeader: IpProtocol = IpProtocol.IPv6_NO_NXT,
    var hopLimit: Byte = 0,
    var sourceAddress: Inet6Address = IPv6.IPV6_NONE,
    var destinationAddress: Inet6Address = IPv6.IPV6_NONE,
    override var payload: IPacket? = null
) : BasePacket() {

    init {
        this.version = 6
        nextHeader = IpProtocol.NONE
        sourceAddress = IPv6.IPV6_NONE
        destinationAddress = IPv6.IPV6_NONE
    }

    override fun serialize(): ByteArray {
        // Get the raw bytes of the payload we encapsulate.
        var payloadData: ByteArray? = null
        payload?.let { payload ->
            payload.parent = this
            payloadData = payload.serialize()
            /*
             * If we forgot to include the IpProtocol before serializing,
             * try to ascertain what it is from the payload. If it's not
             * a payload type we know about, we'll throw an exception.
             */
            if (this.nextHeader == IpProtocol.NONE) {
                var found = false
                val entries = IPv6.nextHeaderClassMap.entries
                for ((key, value) in entries) {
                    if (value().javaClass == payload.javaClass) {
                        found = true
                        nextHeader = key
                        Logger.w("Setting previously unset IPv6 'next header' to $key as detected by payload ${payload.javaClass}")
                        break
                    }
                }
                if (!found) {
                    if (this.payload is Data) {
                        /* we're good -- there shouldn't be an IpProtocol set it it's just data */
                    } else {
                        throw IllegalArgumentException("IpProtocol is unset in IPv6 packet $this. Unable to determine payload type to set for payload ${payload.javaClass}")
                    }
                }
            }
        }
        // Update our internal payload length.
        this.payloadLength = (payloadData?.size ?: 0).toShort()
        // Create a byte buffer to hold the IPv6 packet structure.
        val data = ByteArray(IPv6.HEADER_LENGTH + this.payloadLength)
        val bb = ByteBuffer.wrap(data)
        // Add header fields to the byte buffer in the correct order.
        // Fear not the bit magic that must occur.
        bb.put((this.version.toInt() and 0xF shl 4 or (this.trafficClass.toInt() and 0xF0).ushr(4)).toByte())
        bb.put((this.trafficClass.toInt() and 0xF shl 4 or (this.flowLabel and 0xF0000).ushr(16)).toByte())
        bb.putShort((this.flowLabel and 0xFFFF).toShort())
        bb.putShort(this.payloadLength)
        bb.put(this.nextHeader.ipProtocolNumber.toByte())
        bb.put(this.hopLimit)
        bb.put(this.sourceAddress.address)
        bb.put(this.destinationAddress.address)
        // Add the payload to the byte buffer, if necessary.
        if (payloadData != null)
            bb.put(payloadData)
        // We're done! Return the data.
        return data
    }

    @Throws(PacketParsingException::class)
    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        // Wrap the data in a byte buffer for easier retrieval.
        val bb = ByteBuffer.wrap(data, offset, length)
        // Retrieve values from IPv6 header.
        val firstByte = bb.get()
        val secondByte = bb.get()
        this.version = (firstByte.toInt() and 0xF0).ushr(4).toByte()
        if (this.version.toInt() != 6) {
            throw PacketParsingException(
                "Invalid version for IPv6 packet: " + this.version
            )
        }
        this.trafficClass = (firstByte.toInt() and 0xF shl 4 or (secondByte.toInt() and 0xF0).ushr(4)).toByte()
        this.flowLabel = secondByte.toInt() and 0xF shl 16 or (bb.short.toInt() and 0xFFFF)
        this.payloadLength = bb.short
        this.nextHeader = IpProtocol.of(bb.get().toShort())
        this.hopLimit = bb.get()
        val sourceAddress = ByteArray(16)
        bb.get(sourceAddress, 0, 16)
        val destinationAddress = ByteArray(16)
        bb.get(destinationAddress, 0, 16)
        try {
            this.sourceAddress = Inet6Address.getByAddress(sourceAddress) as Inet6Address
            this.destinationAddress = Inet6Address.getByAddress(destinationAddress) as Inet6Address
        } catch (e: UnknownHostException) {
            throw RuntimeException("Error parsing IPv6 address", e)
        }

        // Retrieve the payload, if possible.
        val payload: IPacket =
            IPv6.nextHeaderClassMap[nextHeader]?.invoke()
                ?: Data()

        // Deserialize as much of the payload as we can (hopefully all of it).
        this.payload = payload.deserialize(
            data, bb.position(),
            Math.min(this.payloadLength.toInt(), bb.limit() - bb.position())
        ).also {
            it.parent = this
        }
        // We're done!
        return this
    }

    companion object {

        val IPV6_NONE = Inet6Address.getByAddress(ByteArray(16)) as Inet6Address

        const val HEADER_LENGTH = 40

        var nextHeaderClassMap: MutableMap<IpProtocol, () -> (IPacket)> = hashMapOf()

        init {
            // TODO: Add ICMPv6, IPv6 Options, etc..
            IPv6.nextHeaderClassMap[IpProtocol.IPv6_ICMP] = { ICMPv6() }
            IPv6.nextHeaderClassMap[IpProtocol.TCP] = { TCP() }
            IPv6.nextHeaderClassMap[IpProtocol.UDP] = { UDP() }
        }
    }
}
