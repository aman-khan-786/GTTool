package com.arman.dev.gttool.util

import java.io.File

object FileHelper {
    
    fun readProcFile(path: String): String? {
        return try {
            File(path).readText()
        } catch (e: Exception) {
            null
        }
    }
    
    fun writeProcFile(path: String, content: String): Boolean {
        return try {
            File(path).writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun fileExists(path: String): Boolean {
        return File(path).exists()
    }
    
    fun readBinaryFile(path: String): ByteArray? {
        return try {
            File(path).readBytes()
        } catch (e: Exception) {
            null
        }
    }
    
    fun writeBinaryFile(path: String, data: ByteArray): Boolean {
        return try {
            File(path).writeBytes(data)
            true
        } catch (e: Exception) {
            false
        }
    }
}