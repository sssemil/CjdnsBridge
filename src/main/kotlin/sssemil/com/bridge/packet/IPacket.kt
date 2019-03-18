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

/**
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
interface IPacket {
    /**
     * @return
     */
    var payload: IPacket?

    /**
     * @return
     */
    var parent: IPacket?

    /**
     * Reset any checksums as needed, and call resetChecksum on all parents
     */
    fun resetChecksum()

    /**
     * Sets all payloads parent packet if applicable, then serializes this
     * packet and all payloads
     *
     * @return a byte[] containing this packet and payloads
     */
    fun serialize(): ByteArray

    /**
     * Deserializes this packet layer and all possible payloads
     *
     * @param data
     * @param offset offset to start deserializing from
     * @param length length of the data to deserialize
     * @return the deserialized data
     */
    @Throws(PacketParsingException::class)
    fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket

    /**
     * Clone this packet and its payload packet but not its parent.
     *
     * @return
     */
    fun clone(): Any
}
