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
import com.sun.jna.ptr.IntByReference
import sssemil.com.socket.interfaces.PipeServerSocket
import sssemil.com.socket.interfaces.PipeSocket
import sssemil.com.socket.interfaces.PipeSocketAddress
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implements a [PipeServerSocket] which binds to a local Unix domain socket and returns instances
 * of [UnixDomainSocket] from [accept].
 */
class UnixDomainServerSocket
@Throws(IOException::class)
@JvmOverloads constructor(backlog: Int = DEFAULT_BACKLOG, path: String? = null) : PipeServerSocket {

    // We use an AtomicInteger to prevent a race in this situation which
    // could happen if fd were just an int:
    //
    // Thread 1 -> UnixDomainServerSocket.accept()
    //          -> lock this
    //          -> check isBoundVar and isClosedVar
    //          -> unlock this
    //          -> descheduled while still in method
    // Thread 2 -> UnixDomainServerSocket.close()
    //          -> lock this
    //          -> check isClosedVar
    //          -> UnixDomainSocketLibrary.close(fd)
    //          -> now fd is invalid
    //          -> unlock this
    // Thread 1 -> re-scheduled while still in method
    //          -> UnixDomainSocketLibrary.accept(fd, which is invalid and maybe re-used)
    //
    // By using an AtomicInteger, we'll set this to -1 after it's closed, which
    // will cause the accept() call above to cleanly fail instead of possibly
    // being called on an unrelated fd (which may or may not fail).
    private val fd: AtomicInteger

    private val backlog: Int
    private var isBoundVar: Boolean = false
    private var isClosedVar: Boolean = false

    init {
        try {
            fd = AtomicInteger(
                    UnixDomainSocketLibrary.socket(
                            UnixDomainSocketLibrary.PF_LOCAL,
                            UnixDomainSocketLibrary.SOCK_STREAM,
                            0))
            this.backlog = backlog
            if (path != null) {
                bind(UnixDomainServerSocketAddress(path))
            }
        } catch (e: LastErrorException) {
            throw IOException(e)
        }

    }

    @Synchronized
    @Throws(IOException::class)
    override fun bind(endpoint: PipeSocketAddress) {
        if (endpoint !is UnixDomainServerSocketAddress) {
            throw IllegalArgumentException(
                    "endpoint must be an instance of UnixDomainServerSocketAddress")
        }
        if (isBoundVar) {
            throw IllegalStateException("Socket is already bound")
        }
        if (isClosedVar) {
            throw IllegalStateException("Socket is already closed")
        }
        val address = SockAddrUn(endpoint.path)
        try {
            val socketFd = fd.get()
            UnixDomainSocketLibrary.bind(socketFd, address, address.size())
            UnixDomainSocketLibrary.listen(socketFd, backlog)
            isBoundVar = true
        } catch (e: LastErrorException) {
            throw IOException(e)
        }

    }

    @Throws(IOException::class)
    override fun accept(): PipeSocket {
        // We explicitly do not make this method synchronized, since the
        // call to UnixDomainSocketLibrary.accept() will block
        // indefinitely, causing another thread's call to close() to deadlock.
        synchronized(this) {
            if (!isBoundVar) {
                throw IllegalStateException("Socket is not bound")
            }
            if (isClosedVar) {
                throw IllegalStateException("Socket is already closed")
            }
        }
        try {
            val sockAddrUn = SockAddrUn()
            val addressLen = IntByReference()
            addressLen.value = sockAddrUn.size()
            val clientFd = UnixDomainSocketLibrary.accept(fd.get(), sockAddrUn, addressLen)
            return UnixDomainSocket(clientFd)
        } catch (e: LastErrorException) {
            throw IOException(e)
        }

    }

    @Synchronized
    @Throws(IOException::class)
    override fun close() {
        if (isClosedVar) {
            throw IllegalStateException("Socket is already closed")
        }
        try {
            // Ensure any pending call to accept() fails.
            UnixDomainSocketLibrary.close(fd.getAndSet(-1))
            isClosedVar = true
        } catch (e: LastErrorException) {
            throw IOException(e)
        }

    }

    /**
     * Constructs and binds a Unix domain server socket to the specified path.
     */
    @Throws(IOException::class)
    constructor(path: String) : this(DEFAULT_BACKLOG, path)

    class UnixDomainServerSocketAddress(val path: String) : PipeSocketAddress

    companion object {

        private const val DEFAULT_BACKLOG = 50
    }
}