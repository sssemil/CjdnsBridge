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

package sssemil.com.net.stack.network

import kotlinx.coroutines.CoroutineScope
import sssemil.com.bridge.socket.EssClientHandle
import sssemil.com.bridge.util.Logger
import sssemil.com.net.stack.Protocol
import sssemil.com.net.stack.exceptions.EmptyPacketException
import sssemil.com.net.stack.exceptions.InvalidTypeException
import sssemil.com.net.stack.network.structures.Ipv6Packet

class Ipv6Protocol(scope: CoroutineScope) : Protocol(scope) {

    override fun swallowFromBelow(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
        handle(handle, buffer.sliceArray(offset until length))
    }

    private fun handle(handle: EssClientHandle, packet: ByteArray) {
        try {
            val ipv6Packet = Ipv6Packet.parse(packet)

            Logger.i(ipv6Packet.toString())

            val returnPacket = Ipv6Packet(
                ipv6Packet.version,
                ipv6Packet.trafficClass,
                ipv6Packet.flowLabel,
                ipv6Packet.payloadLength,
                ipv6Packet.nextHeader,
                ipv6Packet.hopLimit,
                ipv6Packet.destinationAddress,
                ipv6Packet.sourceAddress,
                ipv6Packet.extensionHeaders,
                ipv6Packet.payload
            )

            returnPacket.build().let {
                //spitDown(it, 0, it.size)
            }
        } catch (e: InvalidTypeException) {
            Logger.w("Can't parse packet type!\n")
        } catch (e: EmptyPacketException) {
            Logger.w("Can't parse empty packet!\n")
        }
    }
}