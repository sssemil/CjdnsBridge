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
import sssemil.com.bridge.ess.EssClientHandle
import sssemil.com.bridge.ess.EssSocket
import sssemil.com.bridge.net.stack.Protocol

/**
 * This layer spits IPv6 packets from cjdns.
 *
 * -------------------------------------------------------
 * | Flags (2 bytes) | Proto (2 bytes) | Raw IPv6 packet |
 * -------------------------------------------------------
 *
 * If you don't need packet information, set noPi to true. They you'll get:
 *
 * -------------------
 * | Raw IPv6 packet |
 * -------------------
 *
 * @param path Path to cjdns socket.
 * @param noPi Set to true, to only get raw IP packet.
 */
class CjdnsProtocol(
    scope: CoroutineScope,
    path: String
) : Protocol(scope) {

    private val callback: EssSocket.Callback = object : EssSocket.Callback {

        override fun onNetworkPacket(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
            spitUp(handle, buffer, offset, length)
        }
    }

    private val cjdnsSocket = EssSocket(scope, path, callback)

    override fun swallowFromAbove(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
        cjdnsSocket.clients[handle]?.socket?.write(buffer, offset, length)
    }

    override suspend fun kill() {
        cjdnsSocket.kill()
        super.kill()
    }
}