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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CompletableFuture

class SocketTest {
    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun testAssertEquals() {
        val rand = Random()
        val tempDir = Files.createTempDirectory("ipcsocket")
        val sock = tempDir.resolve("foo" + rand.nextInt() + ".sock")
        val serverSocket = SocketHelper.createServerSocket(sock.toString())

        val server = CompletableFuture.supplyAsync {
            try {
                val echo = serverSocket?.let { EchoServer(it) }
                echo?.run()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            true
        }
        Thread.sleep(100)

        val client = SocketHelper.createSocket(sock.toString())
        val out = PrintWriter(client!!.getOutputStream(), true)
        val `in` = BufferedReader(
                InputStreamReader(client.getInputStream()))
        out.println("hello")
        val line = `in`.readLine()
        client.close()
        server.cancel(true)
        serverSocket!!.close()
        assertEquals("hello", line, "echo did not return the content")
    }
}
