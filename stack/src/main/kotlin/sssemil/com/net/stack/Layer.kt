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

import sssemil.com.common.util.Logger
import sssemil.com.net.interfaces.IClientHandle
import sssemil.com.net.packet.IPacket

class Layer {

    private val protocols = arrayListOf<Protocol>()
    private var upperLayer: Layer? = null
    private var lowerLayer: Layer? = null

    init {
        protocols.forEach { it.layer = this }
    }

    suspend fun kill() {
        protocols.forEach { it.kill() }
    }

    fun bind(upperLayer: Layer) {
        this.upperLayer = upperLayer
        upperLayer.lowerLayer = this
    }

    private fun swallowFromBelow(
        handle: IClientHandle,
        packet: IPacket
    ) {
        protocols.forEach { it.swallowFromBelow(handle, packet) }
    }

    private fun swallowFromAbove(
        handle: IClientHandle,
        packet: IPacket
    ) {
        protocols.forEach { it.swallowFromAbove(handle, packet) }
    }

    fun spitUp(handle: IClientHandle, packet: IPacket) {
        upperLayer?.swallowFromBelow(handle, packet)
    }

    fun spitDown(handle: IClientHandle, packet: IPacket) {
        lowerLayer?.swallowFromAbove(handle, packet)
    }

    fun registerProtocol(protocol: Protocol) {
        if (protocol.layer == null) {
            protocols.add(protocol)
            protocol.layer = this
        } else {
            Logger.e("Already registered in a layer!")
        }
    }
}