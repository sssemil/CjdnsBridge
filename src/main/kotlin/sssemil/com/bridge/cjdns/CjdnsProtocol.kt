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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sssemil.com.bridge.interfaces.ConfigurationCallback
import sssemil.com.bridge.util.Logger
import sssemil.com.net.stack.Protocol
import java.net.Inet6Address
import java.net.UnknownHostException
import java.nio.BufferUnderflowException

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
    path: String,
    private val configurationCallback: ConfigurationCallback,
    private val noPi: Boolean = true
) :
    Protocol(scope) {

    private val cjdnsSocket = CjdnsSocket(scope, path)

    init {
        cjdnsSocket.onAcceptListener = {
            scope.launch {
                withContext(Dispatchers.IO) {
                    onAcceptSocket()
                }
            }
        }
    }

    private fun onAcceptSocket() {
        val buffer = ByteArray(BUFFER_SIZE)
        var readCount: Int
        var position = 0

        do {
            readCount = cjdnsSocket.read(buffer)

            if (readCount < 1) continue

            while (position < readCount) {
                when (buffer[position++]) {
                    TYPE_CJDNS_PACKET -> {
                        Logger.d("CJDNS_PACKET")
                        position += if (noPi) NO_PI_OFFSET else 0

                        spitUp(buffer, position, readCount)
                        position = readCount
                    }
                    TYPE_CONF_ADD_IPV6_ADDRESS -> {
                        try {
                            if (readCount - position >= IPV6_ADDR_LENGTH) {
                                val ipv6 = Inet6Address.getByAddress(
                                    buffer.sliceArray(position until position + IPV6_ADDR_LENGTH)
                                ) as Inet6Address
                                position += IPV6_ADDR_LENGTH
                                Logger.d("CONF_ADD_IPV6_ADDRESS: ${ipv6.hostAddress}")
                                configurationCallback.addAddress(ipv6)
                            } else {
                                Logger.e("Invalid size for an IPv6 address!")
                            }
                        } catch (e: UnknownHostException) {
                            Logger.e("Couldn't parse IPv6 address!", e)
                        }
                    }
                    TYPE_CONF_SET_MTU -> {
                        try {
                            val mtu = byteArrayToInt(buffer, position).toUInt()
                            position += 4
                            Logger.d("CONF_SET_MTU: $mtu")
                            configurationCallback.setMtu(mtu)
                        } catch (e: BufferUnderflowException) {
                            Logger.e("Too short for a valid MTU!", e)
                        }
                    }
                    else -> {
                        Logger.w("Unknown packet type: ${buffer[0]}")
                        position = readCount
                    }
                }
            }
            position = 0
        } while (readCount != -1)

        cjdnsSocket.closeClient()
    }

    override fun swallowFromAbove(buffer: ByteArray, offset: Int, length: Int) {
        cjdnsSocket.write(buffer, offset, length)
    }

    override suspend fun kill() {
        cjdnsSocket.kill()
        super.kill()
    }

    companion object {

        const val BUFFER_SIZE = 2048
        const val NO_PI_OFFSET = 4

        const val IPV6_ADDR_LENGTH = 16

        const val TYPE_CJDNS_PACKET: Byte = 0
        const val TYPE_CONF_ADD_IPV6_ADDRESS: Byte = 1
        const val TYPE_CONF_SET_MTU: Byte = 2

        fun byteArrayToInt(bytes: ByteArray, offset: Int = 0): Int {
            return (bytes[offset + 3].toInt() and 0xFF or (
                    bytes[offset + 2].toInt() and 0xFF shl 8) or (
                    bytes[offset + 1].toInt() and 0xFF shl 16) or (
                    bytes[offset + 0].toInt() and 0xFF shl 24))
        }
    }
}