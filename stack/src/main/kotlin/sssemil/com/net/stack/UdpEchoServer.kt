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

package sssemil.com.net.stack

import kotlinx.coroutines.CoroutineScope
import sssemil.com.net.interfaces.IClientHandle
import sssemil.com.net.packet.IPacket
import sssemil.com.net.packet.IPv6
import sssemil.com.net.packet.UDP

class UdpEchoServer(scope: CoroutineScope) : Protocol(scope) {

    override fun swallowFromBelow(
        handle: IClientHandle,
        packet: IPacket
    ) {
        (((packet as? IPv6)?.payload as? UDP))?.let {
            val tmpAddress = packet.destinationAddress
            packet.destinationAddress = packet.sourceAddress
            packet.sourceAddress = tmpAddress

            val reply = it

            val tmpPort = reply.destinationPort
            reply.destinationPort = reply.sourcePort
            reply.sourcePort = tmpPort

            reply.resetChecksum()

            packet.payload = reply
            packet.resetChecksum()
            spitDown(handle, packet)
        }
    }
}