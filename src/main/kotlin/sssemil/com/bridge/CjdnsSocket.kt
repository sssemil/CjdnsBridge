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

import sssemil.com.bridge.jni.UnixSocketUtils
import sssemil.com.bridge.jni.interfaces.OnAcceptListener
import sssemil.com.bridge.util.Logger
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class CjdnsSocket(path: String) {

    private val keepRunning = AtomicBoolean(true)

    private var socketFd: FileDescriptor? = null

    private val clientLock = Object()

    private var clientFd: FileDescriptor? = null

    private var clientInputStream: FileInputStream? = null
    private var clientOutputStream: FileOutputStream? = null

    /**
     * Get notified on new client.
     */
    var onAcceptListener: (() -> Unit)? = null

    private var socketThread: Thread

    init {
        socketThread = thread {
            val unixSocketUtils = UnixSocketUtils()
            val socketFd = unixSocketUtils.allocate(path)
            this.socketFd = socketFd

            unixSocketUtils.onAccept(socketFd, object : OnAcceptListener {
                override fun accepted(fd: FileDescriptor) {
                    Logger.d("Accepted fd: $fd, valid: ${fd.valid()}")

                    if (fd.valid()) {
                        closeClient()

                        synchronized(clientLock) {

                            clientFd = fd

                            clientInputStream = FileInputStream(clientFd)
                            clientOutputStream = FileOutputStream(clientFd)
                        }

                        onAcceptListener?.invoke()
                    }
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
     * Close all streams with current client.
     */
    fun closeClient() {
        synchronized(clientLock) {
            clientInputStream?.close()
            clientOutputStream?.close()

            clientInputStream = null
            clientOutputStream = null
        }
    }

    /**
     * Write to the socket.
     *
     * @param buffer A buffer containing the packet.
     * @param size the first size bytes will be sent.
     *
     * @return Whether or not there was a client to write to.
     */
    fun write(buffer: ByteArray, offset: Int, length: Int): Boolean {
        synchronized(clientLock) {
            val clientFd = clientFd
            val clientOutputStream = clientOutputStream

            if (clientFd == null || !clientFd.valid() || clientOutputStream == null) {
                Logger.w("There is no valid client yet!")
                return false
            }

            clientOutputStream.write(buffer, offset, length)
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
        synchronized(clientLock) {
            val clientFd = clientFd
            val clientInputStream = clientInputStream

            if (clientFd == null || !clientFd.valid() || clientInputStream == null) {
                Logger.w("There is no valid client yet!")
                return -1
            }

            return clientInputStream.read(buffer)
        }
    }

    companion object {
        private const val THREAD_TIMEOUT = 3000L // 3s
    }
}