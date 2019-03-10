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

class Layer(private vararg val protocols: Protocol) {

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

    private fun swallowFromBelow(buffer: ByteArray, offset: Int, length: Int) {
        protocols.forEach { it.swallowFromBelow(buffer, offset, length) }
    }

    private fun swallowFromAbove(buffer: ByteArray, offset: Int, length: Int) {
        protocols.forEach { it.swallowFromAbove(buffer, offset, length) }
    }

    fun spitUp(buffer: ByteArray, offset: Int, length: Int) {
        upperLayer?.swallowFromBelow(buffer, offset, length)
    }

    fun spitDown(buffer: ByteArray, offset: Int, length: Int) {
        lowerLayer?.swallowFromAbove(buffer, offset, length)
    }
}