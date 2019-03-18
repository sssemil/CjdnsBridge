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

package sssemil.com.bridge.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * From https://stackoverflow.com/a/4114507/3119031.
 */
public class InternetChecksumTest {
    @Test
    public void simplestValidValue() {
        byte[] buf = new byte[1]; // should work for any-length array of zeros
        long expected = 0xFFFF;

        long actual = InternetChecksum.calculateChecksum(buf);

        assertEquals(expected, actual);
    }

    @Test
    public void validSingleByteExtreme() {
        byte[] buf = new byte[]{(byte) 0xFF};
        long expected = 0xFF;

        long actual = InternetChecksum.calculateChecksum(buf);

        assertEquals(expected, actual);
    }

    @Test
    public void validMultiByteExtrema() {
        byte[] buf = new byte[]{0x00, (byte) 0xFF};
        long expected = 0xFF00;

        long actual = InternetChecksum.calculateChecksum(buf);

        assertEquals(expected, actual);
    }

    @Test
    public void validExampleMessage() {
        // Berkley example http://www.cs.berkeley.edu/~kfall/EE122/lec06/tsld023.htm
        // e3 4f 23 96 44 27 99 f3
        byte[] buf = {(byte) 0xe3, 0x4f, 0x23, (byte) 0x96, 0x44, 0x27, (byte) 0x99, (byte) 0xf3};

        long expected = 0x1aff;

        long actual = InternetChecksum.calculateChecksum(buf);

        assertEquals(expected, actual);
    }

    @Test
    public void validExampleEvenMessageWithCarryFromRFC1071() {
        // RFC1071 example http://www.ietf.org/rfc/rfc1071.txt
        // 00 01 f2 03 f4 f5 f6 f7
        byte[] buf = {(byte) 0x00, 0x01, (byte) 0xf2, (byte) 0x03, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7};

        long expected = 0x220d;

        long actual = InternetChecksum.calculateChecksum(buf);

        assertEquals(expected, actual);

    }
}