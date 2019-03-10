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
import sssemil.com.bridge.util.Logger
import sssemil.com.bridge.util.toHexString

class LoggerProtocol(scope: CoroutineScope, val tag: String? = null) : Protocol(scope) {

    override fun swallowFromAbove(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
        Logger.d(
            "${tag
                ?: "LOG"} from above, read count: ${length - offset}, packet: ${buffer.sliceArray(offset until length).toHexString()}"
        )
    }

    override fun swallowFromBelow(handle: EssClientHandle, buffer: ByteArray, offset: Int, length: Int) {
        Logger.d(
            "${tag
                ?: "LOG"} from below, read count: ${length - offset}, packet: ${buffer.sliceArray(offset until length).toHexString()}"
        )
    }
}