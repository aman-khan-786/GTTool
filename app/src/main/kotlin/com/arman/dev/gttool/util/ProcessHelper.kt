package com.arman.dev.gttool.util

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object ProcessHelper {
    
    fun getProcessId(packageName: String): Int {
        return try {
            File("/proc").listFiles()
                ?.filter { it.isDirectory && it.name.all { char -> char.isDigit() } }
                ?.firstOrNull { dir ->
                    val cmdlinePath = File(dir, "cmdline")
                    if (cmdlinePath.exists()) {
                        val cmdline = cmdlinePath.readText().replace("", "")
                        cmdline == packageName
                    } else {
                        false
                    }
                }?.name?.toIntOrNull() ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    fun isProcessRunning(pid: Int): Boolean {
        return File("/proc/$pid").exists()
    }
    
    fun getProcessName(pid: Int): String? {
        return try {
            File("/proc/$pid/cmdline").readText().replace("", "")
        } catch (e: Exception) {
            null
        }
    }
    
    fun getMemoryMaps(pid: Int): List<String> {
        return try {
            BufferedReader(FileReader("/proc/$pid/maps")).use { reader ->
                reader.readLines()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getProcessStatus(pid: Int): String? {
        return try {
            File("/proc/$pid/status").readText()
        } catch (e: Exception) {
            null
        }
    }
}