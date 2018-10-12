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
import kotlin.reflect.jvm.jvmName

abstract class Layer(private val upClass: Class<*>, private val downClass: Class<*>) {

    protected var upLink: Layer? = null

    protected var downLink: Layer? = null

    init {
        Logger.d("Test here for up down classes...")
    }

    abstract fun swallow(buffer: ByteArray, offset: Int, length: Int): Boolean

    fun bindUp(layer: Layer, first: Boolean = true) {
        if (upClass.isAssignableFrom(layer::class.java)) {
            upLink = layer
            if (first) {
                layer.bindDown(this, false)
            }
        } else {
            throw RuntimeException("${upClass.name} required, but got ${layer::class.jvmName}!")
        }
    }

    fun unbindUp(first: Boolean = true) {
        if (first) {
            upLink?.unbindDown(false)
        }
        upLink = null
    }

    fun bindDown(layer: Layer, first: Boolean = true) {
        if (downClass.isAssignableFrom(layer::class.java)) {
            downLink = layer
            if (first) {
                layer.bindUp(this, false)
            }
        } else {
            throw RuntimeException("${downClass.name} required, but got ${layer::class.jvmName}!")
        }
    }

    fun unbindDown(first: Boolean = true) {
        if (first) {
            downLink?.unbindUp(false)
        }
        downLink = null
    }

    open fun kill() {
        upLink?.unbindDown(true)
        downLink?.unbindDown(true)
    }
}