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

package sssemil.com.net

import kotlinx.coroutines.*
import sssemil.com.bridge.util.Logger
import sssemil.com.socket.SocketHelper
import sssemil.com.socket.interfaces.PipeSocket
import java.util.concurrent.atomic.AtomicBoolean

class EssnetSocket(scope: CoroutineScope, path: String) {

    private val keepRunning = AtomicBoolean(true)

    private val socketLock = Object()

    private var socket: PipeSocket? = null

    /**
     * Get notified on new client.
     */
    var onAcceptListener: (() -> Unit)? = null

    private var socketThread: Job

    init {
        socketThread = scope.launch {
            SocketHelper.createServerSocket(path)?.let { socket ->
                while (keepRunning.get()) {
                    withContext(Dispatchers.IO) {
                        socket.accept().let {
                            Logger.d("Accepted client socket: $socket")
                            synchronized(socketLock) {
                                this@EssnetSocket.socket?.close()
                                this@EssnetSocket.socket = it
                            }
                            onAcceptListener?.invoke()
                        }
                    }
                }
            }
        }
    }

    /**
     * Kill the socket by stopping its main thread.
     */
    suspend fun kill() {
        keepRunning.set(false)
        socketThread.cancelAndJoin()
    }

    /**
     * Close current socket.
     */
    fun closeClient() {
        synchronized(socketLock) {
            socket?.close()
        }
    }

    /**
     * Write to the socket.
     *
     * @param buffer A buffer containing the packet.
     * @param length the first size bytes will be sent.
     *
     * @return Whether or not there was a client to write to.
     */
    fun write(buffer: ByteArray, offset: Int, length: Int): Boolean {
        synchronized(socketLock) {
            socket?.outputStream?.write(buffer, offset, length) ?: run {
                Logger.w("There is no valid client yet!")
                return false
            }

            return true
        }
    }

    /**
     * Read from the socket.
     *
     * @param buffer The buffer to read into.
     *
     * @return The number of bytes read.
     */
    fun read(buffer: ByteArray): Int {
        synchronized(socketLock) {
            socket?.inputStream?.let {
                return it.read(buffer)
            } ?: run {
                Logger.w("There is no valid client yet!")
                return -1
            }
        }
    }
}