/*
 * Copyright 2019 Emil Suleymanov
 * Copyright 2011, Big Switch Networks, Inc.
 *
 * Originally created by David Erickson, Stanford University
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

package sssemil.com.bridge.packet

import java.nio.ByteBuffer

/**
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
data class UDP(
    var sourcePort: UShort = 0u,
    var destinationPort: UShort = 0u,
    var length: Short = 0,
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

        this.length = (8 + payloadData.size).toShort()

        val data = ByteArray(this.length.toInt())
        val bb = ByteBuffer.wrap(data)

        bb.putShort(this.sourcePort.toShort()) // UDP packet port numbers are 16 bit
        bb.putShort(this.destinationPort.toShort())
        bb.putShort(this.length)
        bb.putShort(this.checksum)
        bb.put(payloadData)

        // compute checksum if needed
        if (this.checksum.toInt() == 0) {
            bb.rewind()
            var accumulation = 0

            for (i in 0 until this.length / 2) {
                accumulation += 0xffff and bb.short.toInt()
            }
            // pad to an even number of shorts
            if (this.length % 2 > 0) {
                accumulation += bb.get().toInt() and 0xff shl 8
            }

            accumulation = (accumulation shr 16 and 0xffff) + (accumulation and 0xffff)
            this.checksum = (accumulation.inv() and 0xffff).toShort()
            bb.putShort(6, this.checksum)
        }
        return data
    }

    @Throws(PacketParsingException::class)
    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        val bb = ByteBuffer.wrap(data, offset, length)
        this.sourcePort = ((bb.short.toInt() and 0xffff)).toUShort() // short will be signed, pos or neg
        this.destinationPort =
            ((bb.short.toInt() and 0xffff)).toUShort() // convert range 0 to 65534, not -32768 to 32767
        this.length = bb.short
        this.checksum = bb.short

        when {
            UDP.decodeMap.containsKey(this.destinationPort) -> try {
                this.payload = UDP.decodeMap[this.destinationPort]?.getConstructor()?.newInstance()
            } catch (e: Exception) {
                throw RuntimeException("Failure instantiating class", e)
            }
            UDP.decodeMap.containsKey(this.sourcePort) -> try {
                this.payload = UDP.decodeMap[this.sourcePort]?.getConstructor()?.newInstance()
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
