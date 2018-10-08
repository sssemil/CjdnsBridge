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

package sssemil.com.bridge.jni

import sssemil.com.bridge.jni.interfaces.OnAcceptListener
import java.io.FileDescriptor

internal class UnixSocketUtils {

    /**
     * Get pid, uid, and gid of the client.
     */
    external fun idPeer(fd: FileDescriptor): UCred

    /**
     * Allocate a unix domain socket.
     *
     * @param path Path for your new socket.
     */
    external fun allocate(path: String): FileDescriptor

    /**
     * Wait for incoming file descriptor at your socket.
     *
     * @param fd Socket file descriptor from [allocate].
     */
    external fun accept(fd: FileDescriptor): FileDescriptor

    /**
     * Listen to incoming file descriptors. Can be achieved with [subscribe]
     * as well.
     *
     * @param fd Socket file descriptor from [allocate].
     * @param callback Callback to receive incoming file descriptors.
     */
    fun onAccept(fd: FileDescriptor, callback: OnAcceptListener) {
        while (fd.valid() && callback.keepRunning()) {
            accept(fd).let {
                if (it.valid()) {
                    callback.accepted(it)
                }
            }
        }
    }

    /**
     * Subscribe to receive incoming file descriptors on JNI side. Can be achieved with [onAccept]
     * as well, but might come in handy.
     *
     * @param fd Socket file descriptor from [allocate].
     * @param callback Callback to receive incoming file descriptors.
     */
    external fun subscribe(fd: FileDescriptor, callback: OnAcceptListener): FileDescriptor

    companion object {

        init {
            System.loadLibrary("main")
        }
    }
}