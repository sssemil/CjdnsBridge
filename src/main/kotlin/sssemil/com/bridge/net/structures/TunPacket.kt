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

import sssemil.com.bridge.packet.BasePacket
import sssemil.com.bridge.packet.Data
import sssemil.com.bridge.packet.IPacket
import sssemil.com.bridge.packet.IPv6
import java.nio.ByteBuffer

data class TunPacket(var flags: Short = 0, var proto: UShort = ETHERTYPE_IPV6, var frame: IPacket? = null) :
    BasePacket(), IEssPacketPayload {

    override fun serialize(): ByteArray {
        // Get the raw bytes of the payload we encapsulate.
        val frameData = frame?.serialize()
        frame?.parent = this

        // Create a byte buffer to hold the tun frame structure.
        val data = ByteArray(4 + (frameData?.size ?: 0))
        val bb = ByteBuffer.wrap(data)

        // Add header fields to the byte buffer in the correct order.
        bb.putShort(flags)
        bb.putShort(proto.toShort())

        // Add the payload to the byte buffer, if necessary.
        if (frameData != null) {
            bb.put(frameData)
        }

        return data
    }

    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        // Wrap the data in a byte buffer for easier retrieval.
        val bb = ByteBuffer.wrap(data, offset, length)
        // Retrieve values from tun frame header.
        val flags = bb.short
        val proto = bb.short.toUShort()

        // Retrieve the frame
        val frame: IPacket = if (proto == ETHERTYPE_IPV6) IPv6() else Data()

        // Deserialize frame
        this.frame = frame.deserialize(
            data, bb.position(), bb.limit() - bb.position()
        ).also {
            it.parent = this
        }

        return this
    }

    companion object {

        const val ETHERTYPE_IPV6: UShort = 0x86DDu
    }
}