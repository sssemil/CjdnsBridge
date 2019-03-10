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
package sssemil.com.socket.win32;

import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.NotNull;
import sssemil.com.socket.interfaces.PipeServerSocket;
import sssemil.com.socket.interfaces.PipeSocket;
import sssemil.com.socket.interfaces.PipeSocketAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Win32NamedPipeServerSocket implements PipeServerSocket {

    private static final Win32NamedPipeLibrary API = Win32NamedPipeLibrary.INSTANCE;
    private static final String WIN32_PIPE_PREFIX = "\\\\.\\pipe\\";
    private static final int BUFFER_SIZE = 65535;
    private final LinkedBlockingQueue<HANDLE> openHandles;
    private final LinkedBlockingQueue<HANDLE> connectedHandles;
    private final Win32NamedPipeSocket.CloseCallback closeCallback;
    private final String path;
    private final int maxInstances;
    private final HANDLE lockHandle;
    private final boolean requireStrictLength;

    public Win32NamedPipeServerSocket(String path) throws IOException {
        this(Win32NamedPipeLibrary.PIPE_UNLIMITED_INSTANCES, path);
    }

    public Win32NamedPipeServerSocket(int maxInstances, String path) throws IOException {
        this(maxInstances, path, Win32NamedPipeSocket.DEFAULT_REQUIRE_STRICT_LENGTH);
    }

    /**
     * The doc for InputStream#read(byte[] b, int off, int len) states that "An attempt is made to
     * read as many as len bytes, but a smaller number may be read." However, using
     * requireStrictLength, NGWin32NamedPipeSocketInputStream can require that len matches up exactly
     * the number of bytes to read.
     */
    public Win32NamedPipeServerSocket(
            int maxInstances,
            String path,
            boolean requireStrictLength) throws IOException {
        this.openHandles = new LinkedBlockingQueue<>();
        this.connectedHandles = new LinkedBlockingQueue<>();
        this.closeCallback = handle -> {
            if (connectedHandles.remove(handle)) {
                closeConnectedPipe(handle, false);
            }
            if (openHandles.remove(handle)) {
                closeOpenPipe(handle);
            }
        };
        this.maxInstances = maxInstances;
        this.requireStrictLength = requireStrictLength;
        if (!path.startsWith(WIN32_PIPE_PREFIX)) {
            this.path = WIN32_PIPE_PREFIX + path;
        } else {
            this.path = path;
        }
        String lockPath = this.path + "_lock";
        SECURITY_ATTRIBUTES sa = Win32SecurityLibrary
                .createSecurityWithLogonDacl(WinNT.FILE_GENERIC_READ);
        lockHandle = API.CreateNamedPipe(
                lockPath,
                Win32NamedPipeLibrary.FILE_FLAG_FIRST_PIPE_INSTANCE
                        | Win32NamedPipeLibrary.PIPE_ACCESS_DUPLEX,
                0,
                1,
                BUFFER_SIZE,
                BUFFER_SIZE,
                0,
                sa);
        if (lockHandle == Win32NamedPipeLibrary.INVALID_HANDLE_VALUE) {
            throw new IOException(String
                    .format("Could not createServerSocket lock for %s, error %d", lockPath,
                            API.GetLastError()));
        } else {
            if (!API.DisconnectNamedPipe(lockHandle)) {
                throw new IOException(String.format("Could not disconnect lock %d", API.GetLastError()));
            }
        }

    }

    /**
     * The doc for InputStream#read(byte[] b, int off, int len) states that "An attempt is made to
     * read as many as len bytes, but a smaller number may be read." However, using
     * requireStrictLength, Win32NamedPipeSocketInputStream can require that len matches up exactly
     * the number of bytes to read.
     */
    public Win32NamedPipeServerSocket(String path, boolean requireStrictLength) throws IOException {
        this(Win32NamedPipeLibrary.PIPE_UNLIMITED_INSTANCES, path, requireStrictLength);
    }

    private void closeConnectedPipe(HANDLE handle, boolean shutdown) throws IOException {
        if (!shutdown) {
            API.WaitForSingleObject(handle, 10000);
        }
        API.DisconnectNamedPipe(handle);
        API.CloseHandle(handle);
    }

    private void closeOpenPipe(HANDLE handle) throws IOException {
        API.CancelIoEx(handle, null);
        API.CloseHandle(handle);
    }

    public void bind(@NotNull PipeSocketAddress endpoint) throws IOException {
        throw new IOException("Win32 named pipes do not support bind(), pass path to constructor");
    }

    @NotNull
    public PipeSocket accept() throws IOException {
        SECURITY_ATTRIBUTES sa = Win32SecurityLibrary
                .createSecurityWithLogonDacl(WinNT.FILE_ALL_ACCESS);
        HANDLE handle = API.CreateNamedPipe(
                path,
                Win32NamedPipeLibrary.PIPE_ACCESS_DUPLEX | WinNT.FILE_FLAG_OVERLAPPED,
                0,
                maxInstances,
                BUFFER_SIZE,
                BUFFER_SIZE,
                0,
                sa);
        if (handle == Win32NamedPipeLibrary.INVALID_HANDLE_VALUE) {
            throw new IOException(
                    String.format("Could not createServerSocket named pipe, error %d", API.GetLastError()));
        }
        openHandles.add(handle);

        HANDLE connWaitable = API.CreateEvent(null, true, false, null);
        WinBase.OVERLAPPED olap = new WinBase.OVERLAPPED();
        olap.hEvent = connWaitable;
        olap.write();

        boolean immediate = API.ConnectNamedPipe(handle, olap.getPointer());
        if (immediate) {
            openHandles.remove(handle);
            connectedHandles.add(handle);
            return new Win32NamedPipeSocket(handle, closeCallback, requireStrictLength);
        }

        int connectError = API.GetLastError();
        if (connectError == WinError.ERROR_PIPE_CONNECTED) {
            openHandles.remove(handle);
            connectedHandles.add(handle);
            return new Win32NamedPipeSocket(handle, closeCallback, requireStrictLength);
        } else if (connectError == WinError.ERROR_NO_DATA) {
            // Client has connected and disconnected between CreateNamedPipe() and ConnectNamedPipe()
            // connection is broken, but it is returned it avoid loop here.
            // Actual error will happen for NGSession when it will try to read/write from/to pipe
            return new Win32NamedPipeSocket(handle, closeCallback, requireStrictLength);
        } else if (connectError == WinError.ERROR_IO_PENDING) {
            if (!API.GetOverlappedResult(handle, olap.getPointer(), new IntByReference(), true)) {
                openHandles.remove(handle);
                closeOpenPipe(handle);
                throw new IOException(
                        "GetOverlappedResult() failed for connect operation: " + API.GetLastError());
            }
            openHandles.remove(handle);
            connectedHandles.add(handle);
            return new Win32NamedPipeSocket(handle, closeCallback, requireStrictLength);
        } else {
            throw new IOException("ConnectNamedPipe() failed with: " + connectError);
        }
    }

    public void close() throws IOException {
        try {
            List<HANDLE> handlesToClose = new ArrayList<>();
            openHandles.drainTo(handlesToClose);
            for (HANDLE handle : handlesToClose) {
                closeOpenPipe(handle);
            }

            List<HANDLE> handlesToDisconnect = new ArrayList<>();
            connectedHandles.drainTo(handlesToDisconnect);
            for (HANDLE handle : handlesToDisconnect) {
                closeConnectedPipe(handle, true);
            }
        } finally {
            API.CloseHandle(lockHandle);
        }
    }
}
