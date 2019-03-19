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

package sssemil.com.net.packet.icmpv6

import sssemil.com.net.packet.BasePacket
import sssemil.com.net.packet.Data
import sssemil.com.net.packet.IPacket
import java.nio.ByteBuffer

data class EchoRequestMessage(
    var identifier: Short = 0,
    var sequenceNumber: Short = 0,
    override var payload: IPacket? = null
) : BasePacket() {

    override fun serialize(): ByteArray {
        var length = 4
        var payloadData: ByteArray? = null
        payload?.let {
            it.parent = this
            val data = it.serialize()
            length += data.size
            payloadData = data
        }

        val data = ByteArray(length)
        val bb = ByteBuffer.wrap(data)

        bb.putShort(identifier)
        bb.putShort(sequenceNumber)

        if (payloadData != null)
            bb.put(payloadData)

        return data
    }

    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        val bb = ByteBuffer.wrap(data, offset, length)
        identifier = bb.short
        sequenceNumber = bb.short
        payload = Data().deserialize(data, bb.position(), bb.limit() - bb.position())
                .also { parent = this }
        return this
    }
}