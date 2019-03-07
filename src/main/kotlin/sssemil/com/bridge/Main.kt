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
import sssemil.com.bridge.interfaces.ConfigurationCallback
import sssemil.com.bridge.util.Logger
import sssemil.com.bridge.util.toHexString
import sssemil.com.net.layers.Layer
import sssemil.com.net.layers.network.IdentityLayer
import sssemil.com.net.layers.network.NetworkLayer
import java.io.File
import java.lang.System.exit
import java.net.Inet6Address

val configurationCallback = object : ConfigurationCallback {

    override fun addAddress(inet6Address: Inet6Address) {
    }

    override fun setMtu(mtu: UInt) {
    }
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        printUsageAndExit()
    } else {
        val fullPath = args[0]
        val socketFile = File(fullPath)

        if (socketFile.exists()) {
            Logger.w("Specified path is already taken!")
            socketFile.delete()
        }

        Logger.i("Socket path: ${socketFile.absolutePath}")
        exec(File(fullPath))
    }
}

fun exec(socket: File) {
    val layers = ArrayList<Layer>()
    try {
        layers.addAll(
            listOf(
                CjdnsLayer(socket.absolutePath, true, configurationCallback),
                IdentityLayer { buffer, offset, length ->
                    Logger.d("read count: ${length - offset}, packet: ${buffer.sliceArray(offset until length).toHexString()}")
                },
                NetworkLayer()
            )
        )

        for (i in 0 until layers.size - 1) {
            layers[i].bindUp(layers[i + 1])
        }
    } catch (e: Exception) {
        Logger.e("Error", e)
        layers.forEach { it.kill() }
        exit(-1)
    }
}

fun printUsageAndExit() {
    Logger.e("Usage: bridge /path/to/directory/with/socket/essnet")
    exit(-1)
}
