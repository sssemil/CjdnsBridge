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

package sssemil.com.bridge.util

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

object Logger {
    private const val DEBUG = true

    private const val logFileName = "bridge.log"

    private var logOut = PrintWriter(File(logFileName))
    private val logLock = Object()

    var writeLogsToFile = false

    fun d(msg: String) {
        if (DEBUG) {
            val msgWithInfo = "[D][${getTag()}][${getDateString()}]: $msg"

            System.out.println(msgWithInfo)

            if (writeLogsToFile) {
                synchronized(logLock) {
                    logOut.println(msgWithInfo)
                    logOut.flush()
                }
            }
        }
    }

    fun i(msg: String) {
        val msgWithInfo = "[I][${getTag()}][${getDateString()}]: $msg"

        System.out.println(msgWithInfo)

        if (writeLogsToFile) {
            synchronized(logLock) {
                logOut.println(msgWithInfo)
                logOut.flush()
            }
        }
    }

    fun w(msg: String) {
        val msgWithInfo = "[W][${getTag()}][${getDateString()}]: $msg"

        System.out.println(msgWithInfo)

        if (writeLogsToFile) {
            synchronized(logLock) {
                logOut.println(msgWithInfo)
                logOut.flush()
            }
        }
    }

    fun e(msg: String, e: Throwable? = null) {
        var msgWithInfo = "[E][${getTag()}][${getDateString()}]: $msg"

        e?.let {
            msgWithInfo += "${e.message}"
        }

        System.err.println(msgWithInfo)
        e?.printStackTrace()

        if (writeLogsToFile) {
            synchronized(logLock) {
                logOut.println(msgWithInfo)
                e?.printStackTrace(logOut)
                logOut.flush()
            }
        }
        //throw RuntimeException(msgWithInfo)
    }

    private fun getDateString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return dateFormat.format(calendar.time)
    }

    fun getTimeString(deltaTime: Long = System.currentTimeMillis()): String {
        var holder = deltaTime
        val second = holder / 1000 % 60
        holder -= second * 1000
        val minute = holder / (1000 * 60) % 60
        holder -= minute * 1000 * 60
        val hour = holder / (1000 * 60 * 60) % 24
        holder -= hour * 1000 * 60 * 24

        return String.format("%02dh:%02dm:%02ds:%04dms", hour, minute, second, holder)
    }

    fun setOutputDirectory(rootFolder: File) {
        rootFolder.mkdirs()
        val logFile = File(rootFolder, logFileName)
        logOut = PrintWriter(BufferedWriter(FileWriter(logFile, true)))
    }

    /**
     * This function wil give you a tag based on build type.
     *
     * @return tag for logging.
     */
    private fun getTag(): String {
        return if (DEBUG) {
            getCaller()?.let {
                return@let it.className?.substringAfterLast(".") + "#" + it.methodName
            }
        } else {
            getCallerClassName()
        } ?: ""
    }

    /**
     * Gives caller class name. Based on this : https://stackoverflow.com/a/11306854/3119031
     *
     * @return Caller class name
     */
    private fun getCallerClassName() = getCaller()?.className?.substringAfterLast(".")

    /**
     * Gives caller. Based on this : https://stackoverflow.com/a/11306854/3119031
     *
     * @return Caller class name
     */
    private fun getCaller(): StackTraceElement? = Thread.currentThread().stackTrace.firstOrNull {
        it.className != Logger::class.java.name
                && it.className != Thread::class.java.name
                && it.className != "dalvik.system.VMStack"
    }
}
