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

package sssemil.com.net.stack.transport

import kotlinx.coroutines.CoroutineScope
import sssemil.com.bridge.socket.EssClientHandle
import sssemil.com.bridge.util.Logger
import sssemil.com.net.stack.Protocol

class UdpProtocol(scope: CoroutineScope) : Protocol(scope) {

    override fun swallowFromBelow(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
        handle(buffer.sliceArray(offset until length))
    }

    private fun handle(packet: ByteArray) {
        Logger.d("UdpProtocol: Not yet implemented!!!")
    }
}