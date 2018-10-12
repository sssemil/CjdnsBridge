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

import sssemil.com.bridge.util.Logger
import sssemil.com.net.layers.osi.DataLinkLayer

class CjdnsLayer(path: String) : DataLinkLayer() {

    private val cjdnsSocket = CjdnsSocket(path)

    init {
        val buffer = ByteArray(BUFFER_SIZE)
        var readCount: Int

        cjdnsSocket.onAcceptListener = {
            do {
                readCount = cjdnsSocket.read(buffer)

                upLink?.swallow(buffer, 0, readCount) ?: run {
                    Logger.w("No upper layer! Data will be lost.")
                }
            } while (readCount != -1)

            cjdnsSocket.closeClient()
        }
    }

    override fun swallow(buffer: ByteArray, offset: Int, length: Int) = cjdnsSocket.write(buffer, offset, length)

    override fun kill() {
        cjdnsSocket.kill()
        super.kill()
    }

    companion object {
        const val BUFFER_SIZE = 2048
    }
}