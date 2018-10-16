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

import sssemil.com.socket.interfaces.OnAcceptSocketListener
import sssemil.com.socket.SocketHelper
import sssemil.com.socket.onAccept
import sssemil.com.bridge.util.Logger
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class CjdnsSocket(path: String) {

    private val keepRunning = AtomicBoolean(true)

    private val socketLock = Object()

    private var socket: Socket? = null

    /**
     * Get notified on new client.
     */
    var onAcceptListener: (() -> Unit)? = null

    private var socketThread: Thread


    init {
        socketThread = thread {
            SocketHelper.createServerSocket(path)?.onAccept(object : OnAcceptSocketListener {
                override fun accepted(socket: Socket) {
                    Logger.d("Accepted client socket: $socket")
                    synchronized(socketLock) {
                        this@CjdnsSocket.socket?.close()
                        this@CjdnsSocket.socket = socket
                    }
                    onAcceptListener?.invoke()
                }

                override fun keepRunning() = keepRunning.get()
            })
        }
    }

    /**
     * KIll the socket by stopping its main thread.
     */
    fun kill() {
        keepRunning.set(false)
        socketThread.join(THREAD_TIMEOUT)
        socketThread.interrupt()
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
            socket?.getOutputStream()?.write(buffer, offset, length) ?: run {
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
            socket?.getInputStream()?.let {
                return it.read(buffer)
            } ?: run {
                Logger.w("There is no valid client yet!")
                return -1
            }
        }
    }

    companion object {
        private const val THREAD_TIMEOUT = 3000L // 3s
    }
}