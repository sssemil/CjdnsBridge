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
import kotlin.reflect.jvm.jvmName

abstract class Layer(protected open val upClass: Class<*>, protected open val downClass: Class<*>) {

    private var upLinks: LinkedList<Layer> = LinkedList()

    private var downLinks: LinkedList<Layer> = LinkedList()

    init {
        Logger.d("Test here for up down classes...")
    }

    abstract fun swallow(buffer: ByteArray, offset: Int, length: Int): Boolean

    /**
     * Sends to the upper level.
     */
    fun spit(buffer: ByteArray, offset: Int, length: Int) {
        if (upLinks.isEmpty()) {
            Logger.w("No upper layer set! Data will be lost.")
        } else {
            upLinks.forEach { it.swallow(buffer, offset, length) }
        }
    }

    fun bindUp(layer: Layer, first: Boolean = true) {
        if (upClass.isAssignableFrom(layer::class.java)) {
            upLinks.add(layer)
            if (first) {
                layer.bindDown(this, false)
            }
        } else {
            throw RuntimeException("${upClass.name} required, but got ${layer::class.jvmName}!")
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
        if (downClass.isAssignableFrom(layer::class.java)) {
            downLinks.add(layer)
            if (first) {
                layer.bindUp(this, false)
            }
        } else {
            throw RuntimeException("${downClass.name} required, but got ${layer::class.jvmName}!")
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