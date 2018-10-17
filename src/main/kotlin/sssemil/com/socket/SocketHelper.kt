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

package sssemil.com.socket

import sssemil.com.socket.interfaces.OnAcceptSocketListener
import sssemil.com.socket.unix.UnixDomainServerSocket
import sssemil.com.socket.unix.UnixDomainSocket
import sssemil.com.socket.win32.Win32NamedPipeServerSocket
import sssemil.com.socket.win32.Win32NamedPipeSocket
import sssemil.com.platform.utils.OsUtils
import java.net.ServerSocket

object SocketHelper {

    fun createServerSocket(path: String) = when {
        OsUtils.isWin32() -> Win32NamedPipeServerSocket(path)
        OsUtils.isUnix() -> UnixDomainServerSocket(path)
        else -> null
    }

    fun createSocket(path: String) = when {
        OsUtils.isWin32() -> Win32NamedPipeSocket(path)
        OsUtils.isUnix() -> UnixDomainSocket(path)
        else -> null
    }
}

fun ServerSocket.onAccept(callback: OnAcceptSocketListener) {
    while (callback.keepRunning()) {
        accept().let {
            callback.accepted(it)
        }
    }
}