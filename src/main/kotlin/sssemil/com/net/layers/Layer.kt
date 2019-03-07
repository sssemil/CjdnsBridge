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

package sssemil.com.net.layers

import sssemil.com.bridge.util.Logger
import java.util.*

abstract class Layer {

    private var upLinks: LinkedList<Layer> = LinkedList()

    private var downLinks: LinkedList<Layer> = LinkedList()

    init {
        Logger.d("Test here for up down classes...")
    }

    /**
     * Takes from the lower level.
     */
    abstract fun swallowFromBelow(buffer: ByteArray, offset: Int, length: Int): Boolean

    /**
     * Sends to the upper level.
     */
    fun spitUp(buffer: ByteArray, offset: Int, length: Int) {
        if (upLinks.isEmpty()) {
            Logger.w("No upper layer set! Data will be lost.")
        } else {
            upLinks.forEach { it.swallowFromBelow(buffer, offset, length) }
        }
    }

    /**
     * Takes from the upper level.
     */
    abstract fun swallowFromAbove(buffer: ByteArray, offset: Int, length: Int): Boolean

    /**
     * Sends to the lower level.
     */
    fun spitDown(buffer: ByteArray, offset: Int, length: Int) {
        if (downLinks.isEmpty()) {
            Logger.w("No lower layer set! Data will be lost.")
        } else {
            downLinks.forEach { it.swallowFromAbove(buffer, offset, length) }
        }
    }

    fun bindUp(layer: Layer, first: Boolean = true) {
        upLinks.add(layer)
        if (first) {
            layer.bindDown(this, false)
        }
    }

    fun unbindUp(layer: Layer, first: Boolean = true) {
        if (first) {
            upLinks.filter { it == layer }
                .forEach { it.unbindDown(this, false) }
        }
        upLinks.remove(layer)
    }

    fun bindDown(layer: Layer, first: Boolean = true) {
        downLinks.add(layer)
        if (first) {
            layer.bindUp(this, false)
        }
    }

    fun unbindDown(layer: Layer, first: Boolean = true) {
        if (first) {
            downLinks.filter { it == layer }
                .forEach { it.unbindUp(this, false) }
        }
        downLinks.remove(layer)
    }

    open fun kill() {
        upLinks.forEach { it.unbindDown(this, true) }
        downLinks.forEach { it.unbindDown(this, true) }
    }
}