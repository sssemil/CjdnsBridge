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
package sssemil.com.socket.unix

import com.sun.jna.LastErrorException
import sssemil.com.socket.ReferenceCountedFileDescriptor
import sssemil.com.socket.interfaces.PipeSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implements a [PipeSocket] backed by a native Unix domain socket.
 */
class UnixDomainSocket : PipeSocket {

    private val fd: ReferenceCountedFileDescriptor
    override val inputStream: InputStream
    override val outputStream: OutputStream

    /**
     * Creates a Unix domain socket backed by a file path.
     */
    @Throws(IOException::class)
    constructor(path: String) {
        try {
            val fd = AtomicInteger(
                UnixDomainSocketLibrary.socket(
                    UnixDomainSocketLibrary.PF_LOCAL,
                    UnixDomainSocketLibrary.SOCK_STREAM,
                    0
                )
            )
            val address = SockAddrUn(path)
            val socketFd = fd.get()
            UnixDomainSocketLibrary.connect(socketFd, address, address.size())
            this.fd = ReferenceCountedFileDescriptor(socketFd)
            this.inputStream = UnixDomainSocketInputStream()
            this.outputStream = UnixDomainSocketOutputStream()
        } catch (e: LastErrorException) {
            throw IOException(e)
        }
    }

    /**
     * Creates a Unix domain socket backed by a native file descriptor.
     */
    constructor(fd: Int) {
        this.fd = ReferenceCountedFileDescriptor(fd)
        this.inputStream = UnixDomainSocketInputStream()
        this.outputStream = UnixDomainSocketOutputStream()
    }

    @Throws(IOException::class)
    override fun close() {
        try {
            // This might not close the FD right away. In case we are about
            // to read or write on another thread, it will delay the close
            // until the read or write completes, to prevent the FD from
            // being re-used for a different purpose and the other thread
            // reading from a different FD.
            fd.close()
        } catch (e: LastErrorException) {
            throw IOException(e)
        }
    }

    @Throws(IOException::class)
    override fun shutdownInput() {
        doShutdown(UnixDomainSocketLibrary.SHUT_RD)
    }

    @Throws(IOException::class)
    override fun shutdownOutput() {
        doShutdown(UnixDomainSocketLibrary.SHUT_WR)
    }

    @Throws(IOException::class)
    private fun doShutdown(how: Int) {
        try {
            val socketFd = fd.acquire()
            if (socketFd != -1) {
                UnixDomainSocketLibrary.shutdown(socketFd, how)
            }
        } catch (e: LastErrorException) {
            throw IOException(e)
        } finally {
            fd.release()
        }
    }

    private inner class UnixDomainSocketInputStream : InputStream() {

        @Throws(IOException::class)
        override fun read(): Int {
            val buf = ByteBuffer.allocate(1)
            return if (doRead(buf) == 0) {
                -1
            } else {
                // Make sure to & with 0xFF to avoid sign extension
                0xFF and buf.get().toInt()
            }
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            if (len == 0) {
                return 0
            }
            val buf = ByteBuffer.wrap(b, off, len)
            var result = doRead(buf)
            if (result == 0) {
                result = -1
            }
            return result
        }

        @Throws(IOException::class)
        private fun doRead(buf: ByteBuffer): Int {
            try {
                val fdToRead = fd.acquire()
                return if (fdToRead == -1) {
                    -1
                } else UnixDomainSocketLibrary.read(fdToRead, buf, buf.remaining())
            } catch (e: LastErrorException) {
                throw IOException(e)
            } finally {
                fd.release()
            }
        }
    }

    private inner class UnixDomainSocketOutputStream : OutputStream() {

        @Throws(IOException::class)
        override fun write(b: Int) {
            val buf = ByteBuffer.allocate(1)
            buf.put(0, (0xFF and b).toByte())
            doWrite(buf)
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray, off: Int, len: Int) {
            if (len == 0) {
                return
            }
            val buf = ByteBuffer.wrap(b, off, len)
            doWrite(buf)
        }

        @Throws(IOException::class)
        private fun doWrite(buf: ByteBuffer) {
            try {
                val fdToWrite = fd.acquire()
                if (fdToWrite == -1) {
                    return
                }
                val ret = UnixDomainSocketLibrary.write(fdToWrite, buf, buf.remaining())
                if (ret != buf.remaining()) {
                    // This shouldn't happen with standard blocking Unix domain sockets.
                    throw IOException(
                        "Could not write " + buf.remaining() + " bytes as requested " +
                                "(wrote " + ret + " bytes instead)"
                    )
                }
            } catch (e: LastErrorException) {
                throw IOException(e)
            } finally {
                fd.release()
            }
        }
    }
}
