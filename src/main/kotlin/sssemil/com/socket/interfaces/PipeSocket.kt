/*
 * Copyright 2019 Emil Suleymanov
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

package sssemil.com.socket.interfaces

import sssemil.com.bridge.util.Logger
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

abstract class PipeSocket {

    abstract val inputStream: InputStream?

    abstract val outputStream: OutputStream?

    private val socketLock = Object()

    @Throws(IOException::class)
    abstract fun close()

    @Throws(IOException::class)
    abstract fun shutdownInput()

    @Throws(IOException::class)
    abstract fun shutdownOutput()

    /**
     * Close current socket.
     */
    fun closeClient() {
        synchronized(socketLock) {
            close()
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
            outputStream?.write(buffer, offset, length) ?: run {
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
            inputStream?.let {
                return it.read(buffer)
            } ?: run {
                Logger.w("There is no valid client yet!")
                return -1
            }
        }
    }
}