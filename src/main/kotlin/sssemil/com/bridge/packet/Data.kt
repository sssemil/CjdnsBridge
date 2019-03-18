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

import java.util.*

/**
 *
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
data class Data(
    var data: ByteArray = byteArrayOf(),
    override var payload: IPacket? = null
) : BasePacket() {

    override fun serialize(): ByteArray {
        return this.data
    }

    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        this.data = Arrays.copyOfRange(data, offset, offset + length)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Data

        if (!data.contentEquals(other.data)) return false
        if (payload != other.payload) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + (payload?.hashCode() ?: 0)
        return result
    }
}
