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

import sssemil.com.bridge.cjdns.CjdnsLayer
import sssemil.com.bridge.util.Logger
import sssemil.com.bridge.util.toHexString
import sssemil.com.net.layers.Layer
import sssemil.com.net.layers.osi.NetworkLayer
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        printUsageAndExit()
    } else {
        val path = args[0]
        val name = args[1]
        val fullPath = "$path/cjdns_pipe_$name"
        val socketFile = File(fullPath)

        Logger.i("Socket path: ${socketFile.absolutePath}")
        exec(File(fullPath))
    }
}

fun exec(socket: File) {
    val layers = ArrayList<Layer>()
    try {
        layers.add(CjdnsLayer(socket.absolutePath))
        layers.add(object : NetworkLayer() {
            override fun kill() {
            }

            override fun swallow(buffer: ByteArray, offset: Int, length: Int): Boolean {
                Logger.d("read count: ${length - offset}, packet: ${buffer.sliceArray(offset until length).toHexString()}")
                return true
            }
        })

        for (i in layers.indices - 1) {
            layers[i].bindUp(layers[i + 1])
        }
    } catch (e: Exception) {
        Logger.e("Error", e)
        layers.forEach { it.kill() }
        System.exit(-1)
    }
}

fun printUsageAndExit() {
    Logger.e("Usage: bridge /path/to/directory/with/socket socket_name")
    System.exit(-1)
}
