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

import kotlinx.coroutines.*
import sssemil.com.bridge.cjdns.CjdnsProtocol
import sssemil.com.bridge.util.Logger
import sssemil.com.net.stack.Layer
import sssemil.com.net.stack.LoggerProtocol
import sssemil.com.net.stack.network.Ipv6Protocol
import sssemil.com.net.stack.transport.TcpProtocol
import sssemil.com.net.stack.transport.UdpProtocol
import java.io.File
import java.lang.System.exit

val job = SupervisorJob()
val scope = CoroutineScope(Dispatchers.Default + job)

fun main(args: Array<String>) = runBlocking {
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

suspend fun exec(socket: File) {
    val layers = ArrayList<Layer>()
    try {
        val linkLayer = Layer(CjdnsProtocol(scope, socket.absolutePath))
        val networkLayer = Layer(LoggerProtocol(scope, "NET"), Ipv6Protocol(scope))
        val transportLayer = Layer(LoggerProtocol(scope, "TRN"), TcpProtocol(scope), UdpProtocol(scope))

        layers.add(linkLayer)
        layers.add(networkLayer)
        layers.add(transportLayer)

        for (i in 0 until layers.size - 1) {
            layers[i].bind(layers[i + 1])
        }

        job.join()
    } catch (e: Exception) {
        Logger.e("Error", e)
        layers.forEach { it.kill() }
        scope.coroutineContext.cancelChildren()
        exit(-1)
    }
}

fun printUsageAndExit() {
    Logger.e("Usage: bridge /path/to/directory/with/socket/essnet")
    exit(-1)
}
