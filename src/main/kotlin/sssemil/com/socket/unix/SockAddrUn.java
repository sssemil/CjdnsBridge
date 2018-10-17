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

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Bridges {@code struct sockaddr_un} to and from native code.
 */
public class SockAddrUn extends Structure implements Structure.ByReference {

	// BSD platforms write a length byte at the start of struct sockaddr_un.
	private static final boolean HAS_SUN_LEN =
			Platform.isMac() || Platform.isFreeBSD() || Platform.isNetBSD() ||
					Platform.isOpenBSD() || Platform.iskFreeBSD(); 	

	public SunFamily sunFamily = new SunFamily();
	public byte[] sunPath = new byte[104];

	/**
	 * Constructs an empty {@code struct sockaddr_un}.
	 */
	public SockAddrUn() {
		if (HAS_SUN_LEN) {
			sunFamily.sunLenAndFamily = new SunLenAndFamily();
			sunFamily.setType(SunLenAndFamily.class);
		} else {
			sunFamily.setType(Short.TYPE);
		}
		allocateMemory();
	}
	/**
	 * Constructs a {@code struct sockaddr_un} with a path whose bytes are encoded using the default
	 * encoding of the platform.
	 */
	public SockAddrUn(String path) throws IOException {
		byte[] pathBytes = path.getBytes();
		if (pathBytes.length > sunPath.length - 1) {
			throw new IOException(
					"Cannot fit name [" + path + "] in maximum unix domain socket length");
		}
		System.arraycopy(pathBytes, 0, sunPath, 0, pathBytes.length);
		sunPath[pathBytes.length] = (byte) 0;
		if (HAS_SUN_LEN) {
			int len = fieldOffset("sunPath") + pathBytes.length;
			sunFamily.sunLenAndFamily = new SunLenAndFamily();
			sunFamily.sunLenAndFamily.sunLen = (byte) len;
			sunFamily.sunLenAndFamily.sunFamily = UnixDomainSocketLibrary.AF_LOCAL;
			sunFamily.setType(SunLenAndFamily.class);
		} else {
			sunFamily.sunFamily = UnixDomainSocketLibrary.AF_LOCAL;
			sunFamily.setType(Short.TYPE);
		}
		allocateMemory();
	}

	/**
	 * On BSD platforms, the {@code sun_len} and {@code sun_family} values in {@code struct
	 * sockaddr_un}.
	 */
	public static class SunLenAndFamily extends Structure {

		public byte sunLen;
		public byte sunFamily;

		protected List getFieldOrder() {
			return Arrays.asList(new String[]{"sunLen", "sunFamily"});
		}
	}

	/**
	 * On BSD platforms, {@code sunLenAndFamily} will be present. On other platforms, only {@code
	 * sunFamily} will be present.
	 */
	public static class SunFamily extends Union {

		public SunLenAndFamily sunLenAndFamily;
		public short sunFamily;
	}

	protected List getFieldOrder() {
		return Arrays.asList(new String[]{"sunFamily", "sunPath"});
	}
}