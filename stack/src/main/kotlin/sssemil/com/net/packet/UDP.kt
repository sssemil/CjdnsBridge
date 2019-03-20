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
import sssemil.com.net.util.InternetChecksum
import java.nio.ByteBuffer

/**
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
data class UDP(
    var sourcePort: UShort = 0u,
    var destinationPort: UShort = 0u,
    var length: UShort = 0u,
    var checksum: Short = 0,
    override var payload: IPacket? = null
) : BasePacket() {

    override fun resetChecksum() {
        this.checksum = 0
        super.resetChecksum()
    }

    /**
     * Serializes the packet. Will compute and set the following fields if they
     * are set to specific values at the time serialize is called:
     * -checksum : 0
     * -length : 0
     */
    override fun serialize(): ByteArray {
        val payloadData = payload?.let { payload ->
            payload.parent = this
            payload.serialize()
        } ?: byteArrayOf()

        this.length = (8 + payloadData.size).toUShort()

        val data = ByteArray(this.length.toInt())
        val bb = ByteBuffer.wrap(data)

        bb.putShort(this.sourcePort.toShort()) // UDP packet port numbers are 16 bit
        bb.putShort(this.destinationPort.toShort())
        bb.putShort(this.length.toShort())

        // compute checksum if needed
        if (this.checksum.toInt() == 0) {
            if (parent is IPv6) {
                this.checksum = InternetChecksum.checksumHelper(
                    data.sliceArray(0 until bb.position()),
                    payloadData,
                    parent as IPv6,
                    IpProtocol.UDP
                )
            } else {
                Logger.w("Skipping checksum calculation, no IPv6 parent...")
            }
        }

        bb.putShort(this.checksum)
        bb.put(payloadData)
        return data
    }

    @Throws(PacketParsingException::class)
    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        val bb = ByteBuffer.wrap(data, offset, length)
        this.sourcePort = ((bb.short.toInt() and 0xffff)).toUShort() // short will be signed, pos or neg
        this.destinationPort =
                ((bb.short.toInt() and 0xffff)).toUShort() // convert range 0 to 65534, not -32768 to 32767
        this.length = bb.short.toUShort()
        this.checksum = bb.short

        when {
            UDP.Companion.decodeMap.containsKey(this.destinationPort) -> try {
                this.payload = UDP.Companion.decodeMap[this.destinationPort]?.getConstructor()
                        ?.newInstance()
            } catch (e: Exception) {
                throw RuntimeException("Failure instantiating class", e)
            }
            UDP.Companion.decodeMap.containsKey(this.sourcePort) -> try {
                this.payload =
                    UDP.Companion.decodeMap[this.sourcePort]?.getConstructor()?.newInstance()
            } catch (e: Exception) {
                throw RuntimeException("Failure instantiating class", e)
            }
            else -> this.payload = Data()
        }
        this.payload = payload?.deserialize(data, bb.position(), bb.limit() - bb.position())
        this.payload?.parent = this
        return this
    }

    companion object {

        var decodeMap: Map<UShort, Class<out IPacket>> = hashMapOf()
    }
}
