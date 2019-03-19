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
 * @author shudong.zhou@bigswitch.com
 */
data class TCP(
    var sourcePort: UShort = 0u,
    var destinationPort: UShort = 0u,
    var sequence: Int = 0,
    var acknowledge: Int = 0,
    var dataOffset: Byte = 0,
    var flags: Short = 0,
    var windowSize: Short = 0,
    var checksum: Short = 0,
    var urgentPointer: Short = 0,
    var options: ByteArray = byteArrayOf(),
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
        if (dataOffset.toInt() == 0)
            dataOffset = 5  // default header length
        var length = dataOffset.toInt() shl 2
        val payloadData = payload?.let { payload ->
            payload.parent = this
            val data = payload.serialize()
            length += data.size
            data
        } ?: byteArrayOf()

        val data = ByteArray(length)
        val bb = ByteBuffer.wrap(data)

        bb.putShort(this.sourcePort.toShort()) //TCP ports are defined to be 16 bits
        bb.putShort(this.destinationPort.toShort())
        bb.putInt(this.sequence)
        bb.putInt(this.acknowledge)
        bb.putShort((this.flags.toInt() or (dataOffset.toInt() shl 12)).toShort())
        bb.putShort(this.windowSize)
        bb.putShort(this.checksum)
        bb.putShort(this.urgentPointer)
        if (dataOffset > 5) {
            val padding: Int = (dataOffset.toInt() shl 2) - 20 - options.size
            bb.put(options)
            for (i in 0 until padding)
                bb.put(0.toByte())
        }
        bb.put(payloadData)

        // compute checksum if needed
        if (this.checksum.toInt() == 0) {
            bb.rewind()
            var accumulation = 0
            for (i in 0 until length / 2) {
                accumulation += 0xffff and bb.short.toInt()
            }
            // pad to an even number of shorts
            if (length % 2 > 0) {
                accumulation += bb.get().toInt() and 0xff shl 8
            }

            accumulation = (accumulation shr 16 and 0xffff) + (accumulation and 0xffff)
            this.checksum = (accumulation.inv() and 0xffff).toShort()

            bb.putShort(16, this.checksum)
        }

        return data
    }

    @Throws(PacketParsingException::class)
    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        val bb = ByteBuffer.wrap(data, offset, length)
        this.sourcePort = (bb.short.toInt() and 0xffff).toUShort() // short will be signed, pos or neg
        this.destinationPort = (bb.short.toInt() and 0xffff).toUShort() // convert range 0 to 65534, not -32768 to 32767
        this.sequence = bb.int
        this.acknowledge = bb.int
        this.flags = bb.short
        this.dataOffset = (this.flags.toInt() shr 12 and 0xf).toByte()
        if (this.dataOffset < 5) {
            throw PacketParsingException("Invalid tcp header length < 20")
        }
        this.flags = (this.flags.toInt() and 0x1ff).toShort()
        this.windowSize = bb.short
        this.checksum = bb.short
        this.urgentPointer = bb.short
        if (this.dataOffset > 5) {
            var optLength = (dataOffset.toInt() shl 2) - 20
            if (bb.limit() < bb.position() + optLength) {
                optLength = bb.limit() - bb.position()
            }
            try {
                this.options = ByteArray(optLength)
                bb.get(this.options, 0, optLength)
            } catch (e: IndexOutOfBoundsException) {
                this.options = byteArrayOf()
            }

        }

        this.payload = Data()
        val remLength = bb.limit() - bb.position()
        this.payload = payload?.deserialize(data, bb.position(), remLength)

        this.payload?.parent = this
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as TCP

        if (sourcePort != other.sourcePort) return false
        if (destinationPort != other.destinationPort) return false
        if (sequence != other.sequence) return false
        if (acknowledge != other.acknowledge) return false
        if (dataOffset != other.dataOffset) return false
        if (flags != other.flags) return false
        if (windowSize != other.windowSize) return false
        if (checksum != other.checksum) return false
        if (urgentPointer != other.urgentPointer) return false
        if (!options.contentEquals(other.options)) return false
        if (payload != other.payload) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + sourcePort.hashCode()
        result = 31 * result + destinationPort.hashCode()
        result = 31 * result + sequence
        result = 31 * result + acknowledge
        result = 31 * result + dataOffset
        result = 31 * result + flags
        result = 31 * result + windowSize
        result = 31 * result + checksum
        result = 31 * result + urgentPointer
        result = 31 * result + options.contentHashCode()
        result = 31 * result + (payload?.hashCode() ?: 0)
        return result
    }
}
