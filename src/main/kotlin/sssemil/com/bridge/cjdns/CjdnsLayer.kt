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

import sssemil.com.net.layers.Layer

/**
 * This layer spits IPv6 packets from cjdns.
 *
 * -------------------------------------------------------
 * | Flags (2 bytes) | Proto (2 bytes) | Raw IPv6 packet |
 * -------------------------------------------------------
 *
 * If you don't need packet information, set noPi to true. They you'll get:
 *
 * -------------------
 * | Raw IPv6 packet |
 * -------------------
 *
 * @param path Path to cjdns socket.
 * @param noPi Set to true, to only get raw IP packet.
 */
class CjdnsLayer(path: String, noPi: Boolean) : Layer() {

    private val cjdnsSocket = CjdnsSocket(path)

    init {
        val buffer = ByteArray(BUFFER_SIZE)
        var readCount: Int

        cjdnsSocket.onAcceptListener = {
            do {
                readCount = cjdnsSocket.read(buffer)

                spitUp(buffer, if (noPi) NO_PI_OFFSET else 0, readCount)
            } while (readCount != -1)

            cjdnsSocket.closeClient()
        }
    }

    override fun swallowFromBelow(buffer: ByteArray, offset: Int, length: Int) = false

    override fun swallowFromAbove(buffer: ByteArray, offset: Int, length: Int) =
        cjdnsSocket.write(buffer, offset, length)

    override fun kill() {
        cjdnsSocket.kill()
        super.kill()
    }

    companion object {
        const val BUFFER_SIZE = 2048
        const val NO_PI_OFFSET = 4
    }
}