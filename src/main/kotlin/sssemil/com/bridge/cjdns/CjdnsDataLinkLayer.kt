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

package sssemil.com.bridge.cjdns

import sssemil.com.net.layers.osi.IDataLinkLayer

class CjdnsDataLinkLayer : IDataLinkLayer() {
    override fun swallow(buffer: ByteArray, offset: Int, length: Int): Boolean {
        // all is ipv6 ethertype
        spit(buffer, 4, length)
        return true
    }
}