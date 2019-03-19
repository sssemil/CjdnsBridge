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

package sssemil.com.net.util

import java.io.DataOutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

fun DataOutputStream.writeShort(value: Short) {
    writeShort(value.toInt())
}

fun Short.toBytes(): ByteArray {
    val result = ByteArray(2)
    result[1] = toByte()
    result[0] = toInt().shr(8).toByte()
    return result
}

fun Int.toBytes(): ByteArray {
    val result = ByteArray(4)
    result[3] = toByte()
    result[2] = shr(8).toByte()
    result[1] = shr(16).toByte()
    result[0] = shr(24).toByte()
    return result
}

val Socket.isAlive: Boolean
    get() {
        return isBound && isConnected && !isClosed && !isInputShutdown && !isOutputShutdown
    }

fun ByteArray.toBase64(): String {
    return Base64.getEncoder().encode(this).toString(Charset.defaultCharset())
}

fun ByteArray.toHexString(): String {
    val hexString = StringBuffer()
    for (i in indices) {
        val hex = Integer.toHexString(0xff.and(this[i].toInt()))
        if (hex.length == 1) hexString.append('0')
        hexString.append(hex)
    }

    return hexString.toString()
}

fun String.hexStringToByteArray(): ByteArray {
    val len = length
    val data = ByteArray(len / 2)
    var i = 0

    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }

    return data
}