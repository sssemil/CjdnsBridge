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
package sssemil.com.socket.unix;

import com.sun.jna.LastErrorException;
import com.sun.jna.ptr.IntByReference;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements a {@link ServerSocket} which binds to a local Unix domain socket and returns instances
 * of {@link UnixDomainSocket} from {@link #accept()}.
 */
public class UnixDomainServerSocket extends ServerSocket {

	private static final int DEFAULT_BACKLOG = 50;

	// We use an AtomicInteger to prevent a race in this situation which
	// could happen if fd were just an int:
	//
	// Thread 1 -> UnixDomainServerSocket.accept()
	//          -> lock this
	//          -> check isBound and isClosed
	//          -> unlock this
	//          -> descheduled while still in method
	// Thread 2 -> UnixDomainServerSocket.close()
	//          -> lock this
	//          -> check isClosed
	//          -> UnixDomainSocketLibrary.close(fd)
	//          -> now fd is invalid
	//          -> unlock this
	// Thread 1 -> re-scheduled while still in method
	//          -> UnixDomainSocketLibrary.accept(fd, which is invalid and maybe re-used)
	//
	// By using an AtomicInteger, we'll set this to -1 after it's closed, which
	// will cause the accept() call above to cleanly fail instead of possibly
	// being called on an unrelated fd (which may or may not fail).
	private final AtomicInteger fd;

	private final int backlog;
	private boolean isBound;
	private boolean isClosed;

	/**
	 * Constructs an unbound Unix domain server socket.
	 */
	public UnixDomainServerSocket() throws IOException {
		this(DEFAULT_BACKLOG, null);
	}

	/**
	 * Constructs and binds a Unix domain server socket to the specified path with the specified
	 * listen backlog.
	 */
	public UnixDomainServerSocket(int backlog, String path) throws IOException {
		try {
			fd = new AtomicInteger(
					UnixDomainSocketLibrary.socket(
							UnixDomainSocketLibrary.PF_LOCAL,
							UnixDomainSocketLibrary.SOCK_STREAM,
							0));
			this.backlog = backlog;
			if (path != null) {
				bind(new UnixDomainServerSocketAddress(path));
			}
		} catch (LastErrorException e) {
			throw new IOException(e);
		}
	}

	public synchronized void bind(SocketAddress endpoint) throws IOException {
		if (!(endpoint instanceof UnixDomainServerSocketAddress)) {
			throw new IllegalArgumentException(
					"endpoint must be an instance of UnixDomainServerSocketAddress");
		}
		if (isBound) {
			throw new IllegalStateException("Socket is already bound");
		}
		if (isClosed) {
			throw new IllegalStateException("Socket is already closed");
		}
		UnixDomainServerSocketAddress unEndpoint = (UnixDomainServerSocketAddress) endpoint;
		UnixDomainSocketLibrary.SockaddrUn address =
				new UnixDomainSocketLibrary.SockaddrUn(unEndpoint.getPath());
		try {
			int socketFd = fd.get();
			UnixDomainSocketLibrary.bind(socketFd, address, address.size());
			UnixDomainSocketLibrary.listen(socketFd, backlog);
			isBound = true;
		} catch (LastErrorException e) {
			throw new IOException(e);
		}
	}

	public Socket accept() throws IOException {
		// We explicitly do not make this method synchronized, since the
		// call to UnixDomainSocketLibrary.accept() will block
		// indefinitely, causing another thread's call to close() to deadlock.
		synchronized (this) {
			if (!isBound) {
				throw new IllegalStateException("Socket is not bound");
			}
			if (isClosed) {
				throw new IllegalStateException("Socket is already closed");
			}
		}
		try {
			UnixDomainSocketLibrary.SockaddrUn sockaddrUn =
					new UnixDomainSocketLibrary.SockaddrUn();
			IntByReference addressLen = new IntByReference();
			addressLen.setValue(sockaddrUn.size());
			int clientFd = UnixDomainSocketLibrary.accept(fd.get(), sockaddrUn, addressLen);
			return new UnixDomainSocket(clientFd);
		} catch (LastErrorException e) {
			throw new IOException(e);
		}
	}

	public synchronized void close() throws IOException {
		if (isClosed) {
			throw new IllegalStateException("Socket is already closed");
		}
		try {
			// Ensure any pending call to accept() fails.
			UnixDomainSocketLibrary.close(fd.getAndSet(-1));
			isClosed = true;
		} catch (LastErrorException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Constructs an unbound Unix domain server socket with the specified listen backlog.
	 */
	public UnixDomainServerSocket(int backlog) throws IOException {
		this(backlog, null);
	}

	/**
	 * Constructs and binds a Unix domain server socket to the specified path.
	 */
	public UnixDomainServerSocket(String path) throws IOException {
		this(DEFAULT_BACKLOG, path);
	}

	public static class UnixDomainServerSocketAddress extends SocketAddress {

		private final String path;

		public UnixDomainServerSocketAddress(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}
}
