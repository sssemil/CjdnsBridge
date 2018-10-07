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

package sssemil.com.bridge

import sssemil.com.bridge.util.Logger
import sssemil.com.bridge.util.toHexString
import java.io.IOException

fun main(args: Array<String>) {
    var cjdnsSocket: CjdnsSocket? = null
    try {
        cjdnsSocket = CjdnsSocket("/home/emil/WorkingFolder/socket")
        val buffer = ByteArray(2048)
        var readCount: Int

        cjdnsSocket.onAcceptListener = {
            do {
                readCount = cjdnsSocket.read(buffer)

                Logger.i("read count: $readCount, packet: ${buffer.sliceArray(0 until readCount).toHexString()}")
            } while (readCount != -1)

            cjdnsSocket.closeClient()
        }
    } catch (e: IOException) {
        Logger.e("IOExc", e)
        cjdnsSocket?.kill()
    }
}