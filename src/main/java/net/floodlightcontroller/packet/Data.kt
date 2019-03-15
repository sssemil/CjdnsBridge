/**
 * Copyright 2011, Big Switch Networks, Inc.
 * Originally created by David Erickson, Stanford University
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.floodlightcontroller.packet

import java.util.Arrays

/**
 *
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
class Data(var data: ByteArray = byteArrayOf()) : BasePacket() {

    override fun serialize(): ByteArray {
        return this.data
    }

    override fun deserialize(data: ByteArray, offset: Int, length: Int): IPacket {
        this.data = Arrays.copyOfRange(data, offset, offset + length)
        return this
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    override fun hashCode(): Int {
        val prime = 1571
        var result = super.hashCode()
        result = prime * result + Arrays.hashCode(data)
        return result
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    override fun equals(obj: Any?): Boolean {
        if (this === obj)
            return true
        if (!super.equals(obj))
            return false
        if (obj !is Data)
            return false
        val other = obj as Data?
        return if (!Arrays.equals(data, other!!.data)) false else true
    }

    override fun toString(): String {
        return "[${data.size}]"
    }
}
