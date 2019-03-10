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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sssemil.com.bridge.ess.EssClientHandle
import sssemil.com.bridge.net.structures.Ipv6Packet
import java.net.Inet6Address

class DummyLinkProtocol(scope: CoroutineScope, private val delayLength: Long = 1000) : Protocol(scope) {

    init {
        scope.launch {
            val ipv6Loopback = Inet6Address.getByName("::1") as Inet6Address
            val serialized = Ipv6Packet(
                destinationAddress = ipv6Loopback,
                sourceAddress = ipv6Loopback,
                hopLimit = 16
            ).build()

            val handle = EssClientHandle()

            while (true) {
                spitUp(handle, serialized, 0, serialized.size)
                delay(delayLength)
            }
        }
    }
}