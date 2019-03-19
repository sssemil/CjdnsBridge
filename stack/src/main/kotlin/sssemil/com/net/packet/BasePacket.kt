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

/**
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
abstract class BasePacket(
    override var payload: IPacket? = null,
    override var parent: IPacket? = null
) : IPacket {

    override fun resetChecksum() {
        this.parent?.resetChecksum()
    }

    override fun clone(): Any {
        val pkt: IPacket
        try {
            pkt = this.javaClass.newInstance()
        } catch (e: Exception) {
            throw RuntimeException("Could not clone packet")
        }

        // TODO: we are using serialize()/deserialize() to perform the
        // cloning. Not the most efficient way but simple. We can revisit
        // if we hit performance problems.
        val data = this.serialize()
        try {
            pkt.deserialize(this.serialize(), 0, data.size)
        } catch (e: PacketParsingException) {
            // This shouldn't happen here, since we already deserialized it once
            return Data(data)
        }

        pkt.parent = this.parent
        return pkt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasePacket

        if (payload != other.payload) return false

        return true
    }

    override fun hashCode(): Int {
        return payload?.hashCode() ?: 0
    }
}
