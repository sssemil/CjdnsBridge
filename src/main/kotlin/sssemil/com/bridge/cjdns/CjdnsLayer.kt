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

package sssemil.com.bridge.cjdns

import sssemil.com.bridge.interfaces.ConfigurationCallback
import sssemil.com.bridge.util.Logger
import sssemil.com.net.layers.Layer
import java.net.Inet6Address
import java.net.UnknownHostException
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

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
class CjdnsLayer(path: String, noPi: Boolean, configurationCallback: ConfigurationCallback) : Layer() {

    private val cjdnsSocket = CjdnsSocket(path)

    init {
        val bufferArray = ByteArray(BUFFER_SIZE)
        var readCount: Int

        cjdnsSocket.onAcceptListener = {
            do {
                readCount = cjdnsSocket.read(bufferArray)
                val buffer = ByteBuffer.wrap(bufferArray)

                if (readCount >= 1) {
                    when (buffer.get()) {
                        TYPE_CJDNS_PACKET ->
                            spitUp(
                                ByteArray(buffer.remaining()) { buffer.get() },
                                PREFIX_SIZE + if (noPi) NO_PI_OFFSET else 0,
                                readCount
                            )
                        TYPE_CONF_ADD_IPV6_ADDRESS ->
                            try {
                                val ipv6 = Inet6Address.getByAddress(
                                    ByteArray(16) { buffer.get() }
                                ) as Inet6Address
                                Logger.d("CONF_ADD_IPV6_ADDRESS: ${ipv6.hostAddress}")
                                configurationCallback.addAddress(ipv6)
                            } catch (e: BufferUnderflowException) {
                                Logger.e("Too short for a valid IPv6 address!", e)
                            } catch (e: UnknownHostException) {
                                Logger.e("Couldn't parse IPv6 address!", e)
                            }
                        TYPE_CONF_SET_MTU -> {
                            try {
                                val mtu = buffer.int.toUInt()
                                Logger.d("CONF_SET_MTU: $mtu")
                                configurationCallback.setMtu(mtu)
                            } catch (e: BufferUnderflowException) {
                                Logger.e("Too short for a valid MTU!", e)
                            }
                        }
                        else ->
                            Logger.w("Unknown packet type: ${bufferArray[0]}")
                    }
                }
            } while (readCount != -1)

            cjdnsSocket.closeClient()
        }
    }

    override fun swallowFromBelow(buffer: ByteArray, offset: Int, length: Int) = false

    override fun swallowFromAbove(buffer: ByteArray, offset: Int, length: Int) =
        cjdnsSocket.write(buffer, offset, length)

    override fun kill() {
        cjdnsSocket.kill()
        super.kill()
    }

    companion object {
        const val PREFIX_SIZE = 1
        const val BUFFER_SIZE = 2048
        const val NO_PI_OFFSET = 4

        const val TYPE_CJDNS_PACKET: Byte = 0
        const val TYPE_CONF_ADD_IPV6_ADDRESS: Byte = 1
        const val TYPE_CONF_SET_MTU: Byte = 2
    }
}