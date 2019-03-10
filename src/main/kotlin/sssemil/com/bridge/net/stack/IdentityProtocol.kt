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

package sssemil.com.bridge.net.stack

import kotlinx.coroutines.CoroutineScope
import sssemil.com.bridge.ess.EssClientHandle

class IdentityProtocol(
    scope: CoroutineScope,
    private val function: ((buffer: ByteArray, offset: Int, length: Int) -> Unit)? = null
) :
    Protocol(scope) {

    override fun swallowFromAbove(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
        function?.invoke(buffer, offset, length)
        spitDown(handle, buffer, offset, length)
    }

    override fun swallowFromBelow(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
        function?.invoke(buffer, offset, length)
        spitUp(handle, buffer, offset, length)
    }
}