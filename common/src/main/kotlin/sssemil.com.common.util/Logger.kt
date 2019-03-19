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

package sssemil.com.common.util

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

object Logger {

    var loggingLevel = Level.Verbose
    var writeLogsToFile = false

    private const val logFileName = "bridge.log"

    private val logLock = Object()
    private var logOut = PrintWriter(File(logFileName))

    fun d(msg: String) {
        write(Logger.Level.Debug, msg)
    }

    fun i(msg: String) {
        write(Logger.Level.Info, msg)
    }

    fun w(msg: String) {
        write(Logger.Level.Warn, msg)
    }

    fun e(msg: String, tr: Throwable? = null) {
        write(Logger.Level.Error, msg, tr)
    }

    private fun write(level: Level, msg: String? = null, tr: Throwable? = null) {
        if (loggingLevel.value >= level.value) {
            var msgWithInfo = "[${getDateString()}][${level.mark}][${getCallerClassName()}]: $msg"

            tr?.let {
                msgWithInfo += "${tr.message}"
            }

            (if (level == Logger.Level.Error) System.err else System.out).println(msgWithInfo)
            tr?.printStackTrace()

            if (writeLogsToFile) {
                synchronized(logLock) {
                    logOut.println(msgWithInfo)
                    tr?.printStackTrace(logOut)
                    logOut.flush()
                }
            }
        }
    }

    private fun getDateString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return dateFormat.format(calendar.time)
    }

    fun setOutputDirectory(rootFolder: File) {
        rootFolder.mkdirs()
        val logFile = File(rootFolder, logFileName)
        logOut = PrintWriter(BufferedWriter(FileWriter(logFile, true)))
    }

    /**
     * Gives caller class name with line number and function name. Based on this : https://stackoverflow.com/a/11306854/3119031
     *
     * @return Caller class name
     */
    private fun getCallerClassName() = getCaller()?.let {
        it.className + "(" + it.fileName + ":" + it.lineNumber + ")"
    } ?: "#"

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

    enum class Level(val value: Int, val mark: String) {
        Off(0, "-"),
        Error(1, "E"),
        Warn(2, "W"),
        Info(3, "I"),
        Debug(4, "D"),
        Verbose(5, "V")
    }
}
