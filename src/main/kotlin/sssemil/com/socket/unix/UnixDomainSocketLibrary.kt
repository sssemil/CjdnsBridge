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

import com.sun.jna.*
import com.sun.jna.ptr.IntByReference
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*

/**
 * Utility class to bridge native Unix domain socket calls to Java using JNA.
 */
object UnixDomainSocketLibrary {

    public const val PF_LOCAL = 1
    public const val AF_LOCAL = 1
    public const val SOCK_STREAM = 1

    public const val SHUT_RD = 0
    public const val SHUT_WR = 1

    init {
        Native.register(Platform.C_LIBRARY_NAME)
    }

    @Throws(LastErrorException::class)
    external fun socket(domain: Int, type: Int, protocol: Int): Int

    @Throws(LastErrorException::class)
    external fun bind(fd: Int, address: SockAddrUn, addressLen: Int): Int

    @Throws(LastErrorException::class)
    external fun listen(fd: Int, backlog: Int): Int

    @Throws(LastErrorException::class)
    external fun accept(fd: Int, address: SockAddrUn, addressLen: IntByReference): Int

    @Throws(LastErrorException::class)
    external fun connect(fd: Int, address: SockAddrUn, addressLen: Int): Int

    @Throws(LastErrorException::class)
    external fun read(fd: Int, buffer: ByteBuffer, count: Int): Int

    @Throws(LastErrorException::class)
    external fun write(fd: Int, buffer: ByteBuffer, count: Int): Int

    @Throws(LastErrorException::class)
    external fun close(fd: Int): Int

    @Throws(LastErrorException::class)
    external fun shutdown(fd: Int, how: Int): Int
}