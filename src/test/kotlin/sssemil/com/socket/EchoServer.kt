/*
 * Copyright 2018 Emil Suleymanov
 * Copyright 2004-2015, Martian Software, Inc.
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

package sssemil.com.socket

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.CompletableFuture

class EchoServer(private val serverSocket: ServerSocket) {

    @Throws(IOException::class)
    fun run() {
        while (true) {
            val clientSocket = serverSocket.accept()
            CompletableFuture.supplyAsync {
                try {
                    val writer = PrintWriter(clientSocket.getOutputStream(), true)
                    val reader = BufferedReader(
                            InputStreamReader(clientSocket.getInputStream()))
                    var line: String?
                    do {
                        line = reader.readLine()
                        if (line != null) {
                            println("server: $line")
                            writer.println(line)
                        }
                    } while (line!!.trim { it <= ' ' } != "bye")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                true
            }
        }
    }
}
