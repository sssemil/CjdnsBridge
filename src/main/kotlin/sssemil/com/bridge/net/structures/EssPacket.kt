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

package sssemil.com.bridge.net.structures

import sssemil.com.bridge.packet.IPv6
import sssemil.com.bridge.util.Logger
import java.net.Inet6Address
import java.net.UnknownHostException
import java.nio.BufferUnderflowException

data class EssPacket(val type: Byte, val payload: IEssPacketPayload) {

    companion object {

        const val TYPE_TUN_PACKET: Byte = 0
        const val TYPE_CONF_ADD_IPV6_ADDRESS: Byte = 1
        const val TYPE_CONF_SET_MTU: Byte = 2

        private const val IPV6_ADDR_LENGTH = 16

        fun parse(data: DataBitStream): EssPacket? {
            data.takeByte().let { type ->
                when (type) {
                    TYPE_TUN_PACKET -> {
                        val length = data.takeUInt() - 4u
                        val flags = data.takeShort()
                        val proto = data.takeShort().toUShort()

                        Logger.d("TUN_PACKET: [length: $length, flags: $flags, proto: $proto](${data.remainingBits() / 8})")

                        val frameData = data.takeByteArray(Math.min(data.remainingBits() / 8, length.toLong().toInt()))
                        return EssPacket(
                            TYPE_TUN_PACKET,
                            TunPacket(flags, proto, IPv6().deserialize(frameData, 0, frameData.size))
                        )
                    }
                    TYPE_CONF_ADD_IPV6_ADDRESS -> {
                        try {
                            val inet6Address = Inet6Address.getByAddress(
                                data.takeByteArray(IPV6_ADDR_LENGTH)
                            ) as Inet6Address
                            Logger.d("CONF_ADD_IPV6_ADDRESS: ${inet6Address.hostAddress}")
                            return EssPacket(TYPE_CONF_ADD_IPV6_ADDRESS, EssAddIpv6AddressPayload(inet6Address))
                        } catch (e: UnknownHostException) {
                            Logger.e("Couldn't parse IPv6 address!", e)
                        } catch (e: InsufficientBitsException) {
                            Logger.e("Invalid size for an IPv6 address!")
                        }
                    }
                    TYPE_CONF_SET_MTU -> {
                        try {
                            val mtu = data.takeUInt()
                            Logger.d("CONF_SET_MTU: $mtu")
                            return EssPacket(TYPE_CONF_SET_MTU, EssSetMtuPayload(mtu))
                        } catch (e: BufferUnderflowException) {
                            Logger.e("Too short for a valid MTU!", e)
                        }
                    }
                    else -> {
                        Logger.w("Unknown packet type: $type")
                        data.discard()
                    }
                }
            }

            return null
        }
    }
}