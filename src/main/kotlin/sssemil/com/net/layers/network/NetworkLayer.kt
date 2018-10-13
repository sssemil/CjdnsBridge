/*
 * Copyright 2018 Emil Suleymanov
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

package sssemil.com.net.layers.network

import sssemil.com.bridge.util.Logger
import sssemil.com.net.layers.network.exceptions.InvalidTypeException
import sssemil.com.net.layers.network.structures.Ipv6Packet
import sssemil.com.net.layers.osi.INetworkLayer

class NetworkLayer : INetworkLayer() {

    override fun swallow(buffer: ByteArray, offset: Int, length: Int): Boolean {
        handle(buffer.sliceArray(offset until length))
        return true
    }

    private fun handle(packet: ByteArray) {
        try {
            val ipv6Packet = Ipv6Packet.parse(packet)

            Logger.i(ipv6Packet.toString())

            spit(packet, 0, packet.size)
        } catch (e: InvalidTypeException) {
            Logger.e("Can't parse packet!\n", e)
        }
    }
}