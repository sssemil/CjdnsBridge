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

import kotlinx.coroutines.CoroutineScope
import sssemil.com.bridge.util.Logger

abstract class Protocol(val scope: CoroutineScope) {

    var layer: Layer? = null

    init {
        Logger.d("Test here for up down classes...")
    }

    /**
     * Takes from the lower level.
     */
    open fun swallowFromBelow(buffer: ByteArray, offset: Int, length: Int) {
        // not implemented
    }

    /**
     * Sends to the upper level.
     */
    open fun spitUp(buffer: ByteArray, offset: Int, length: Int) {
        layer?.spitUp(buffer, offset, length) ?: run {
            Logger.w("No upper layer set! Data will be lost.")
        }
    }

    /**
     * Takes from the upper level.
     */
    open fun swallowFromAbove(buffer: ByteArray, offset: Int, length: Int) {
        // not implemented
    }

    /**
     * Sends to the lower level.
     */
    open fun spitDown(buffer: ByteArray, offset: Int, length: Int) {
        layer?.spitDown(buffer, offset, length) ?: run {
            Logger.w("No lower layer set! Data will be lost.")
        }
    }

    open suspend fun kill() {
        layer = null
    }
}