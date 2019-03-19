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

package sssemil.com.bridge.ess

import kotlinx.coroutines.*
import sssemil.com.common.util.Logger
import sssemil.com.net.structures.*
import sssemil.com.socket.SocketHelper
import java.util.concurrent.atomic.AtomicBoolean

class EssSocket(
    scope: CoroutineScope,
    path: String,
    private val callback: Callback
) {

    private val keepRunning = AtomicBoolean(true)

    val clients = hashMapOf<EssClientHandle, EssClient>()

    private var socketThread: Job

    init {
        socketThread = scope.launch {
            SocketHelper.createServerSocket(path)?.let { socket ->
                while (keepRunning.get()) {
                    withContext(Dispatchers.IO) {
                        socket.accept().let {
                            val client = EssClient(socket = it)
                            Logger.d("Accepted client socket: $client")
                            clients[client.handle] = client
                            onAcceptClient(client)
                        }
                    }
                }
            }
        }
    }

    private fun onAcceptClient(client: EssClient) {
        var bufferSize: Int = BUFFER_SIZE
        var buffer = ByteArray(bufferSize)
        var readCount: Int

        do {
            readCount = client.socket.read(buffer)

            if (readCount < 1) continue

            val data = DataBitStream(buffer, 0, readCount)

            while (!data.isEmpty()) {
                EssPacket.parse(data)?.let {
                    when (it.type) {
                        EssPacket.TYPE_TUN_PACKET -> {
                            callback.onPacket(client.handle, it.payload as TunPacket)
                        }
                        EssPacket.TYPE_CONF_ADD_IPV6_ADDRESS -> {
                            client.addresses.add((it.payload as EssAddIpv6AddressPayload).inet6Address)
                        }
                        EssPacket.TYPE_CONF_SET_MTU -> {
                            client.mtu = ((it.payload as EssSetMtuPayload).mtu)
                        }
                        else -> {
                            /* welp */
                        }
                    }
                }
            }

            if (client.mtu.toInt() != bufferSize) {
                bufferSize = client.mtu.toInt()
                buffer = ByteArray(bufferSize)
            }
        } while (readCount != -1)

        client.socket.closeClient()
        clients.remove(client.handle)
    }

    /**
     * Kill the socket by stopping its main thread.
     */
    suspend fun kill() {
        keepRunning.set(false)
        socketThread.cancelAndJoin()
    }

    interface Callback {

        fun onPacket(handle: EssClientHandle, packet: TunPacket)
    }

    companion object {

        const val BUFFER_SIZE = 2048
    }
}