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

package sssemil.com.bridge.cjdns

import kotlinx.coroutines.CoroutineScope
import net.floodlightcontroller.packet.IPacket
import sssemil.com.bridge.ess.EssClientHandle
import sssemil.com.bridge.ess.EssSocket
import sssemil.com.bridge.net.stack.Protocol
import sssemil.com.bridge.net.structures.TunPacket

/**
 * This layer spits IPv6 packets from cjdns.
 *
 * @param path Path to cjdns socket.
 */
class CjdnsProtocol(
    scope: CoroutineScope,
    path: String
) : Protocol(scope) {

    private val callback: EssSocket.Callback = object : EssSocket.Callback {

        override fun onPacket(packet: TunPacket) {
            //TODO: Use proper handle.
            spitUp(EssClientHandle(), packet.frame)
        }
    }

    private val cjdnsSocket = EssSocket(scope, path, callback)

    override fun swallowFromAbove(handle: EssClientHandle, packet: IPacket) {
        val buffer = packet.serialize()
        cjdnsSocket.clients[handle]?.socket?.write(buffer, 0, buffer.size)
    }

    override suspend fun kill() {
        cjdnsSocket.kill()
        super.kill()
    }
}