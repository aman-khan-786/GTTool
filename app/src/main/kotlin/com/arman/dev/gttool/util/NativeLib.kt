package com.arman.dev.gttool.util

import android.util.Log

/**
 * Native C++ Memory Operations
 * Provides JNI bridge to native memory scanning functions
 */
class NativeLib {
    
    companion object {
        private const val TAG = "NativeLib"
        private var isNativeLoaded = false
        
        init {
            try {
                System.loadLibrary("native-lib")
                isNativeLoaded = true
                Log.d(TAG, "✅ Native library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                isNativeLoaded = false
                Log.e(TAG, "❌ Failed to load native library", e)
            }
        }
        
        fun isNativeAvailable(): Boolean = isNativeLoaded
    }
    
    /**
     * Scan memory region for specific byte pattern
     * @param pid Process ID to scan
     * @param startAddress Start address of memory region
     * @param endAddress End address of memory region
     * @param searchBytes Byte pattern to search for
     * @return Array of addresses where pattern was found, null if error
     */
    external fun scanMemoryRegion(
        pid: Int,
        startAddress: Long,
        endAddress: Long,
        searchBytes: ByteArray
    ): LongArray?
    
    /**
     * Read memory from target process
     * @param pid Process ID to read from
     * @param address Memory address to read
     * @param size Number of bytes to read
     * @return Byte array of read data, null if error
     */
    external fun readMemory(
        pid: Int,
        address: Long,
        size: Int
    ): ByteArray?
    
    /**
     * Write memory to target process
     * @param pid Process ID to write to
     * @param address Memory address to write
     * @param data Byte array to write
     * @return true if successful, false otherwise
     */
    external fun writeMemory(
        pid: Int,
        address: Long,
        data: ByteArray
    ): Boolean
    
    /**
     * Get memory maps of target process
     * Reads /proc/[pid]/maps
     * @param pid Process ID
     * @return String containing memory maps, null if error
     */
    external fun getProcessMaps(pid: Int): String?
    
    /**
     * Scan for integer value in memory
     * @param pid Process ID
     * @param value Integer value to search
     * @return Array of addresses where value was found
     */
    external fun scanInt(pid: Int, value: Int): LongArray?
    
    /**
     * Scan for long value in memory
     * @param pid Process ID
     * @param value Long value to search
     * @return Array of addresses where value was found
     */
    external fun scanLong(pid: Int, value: Long): LongArray?
    
    /**
     * Scan for float value in memory
     * @param pid Process ID
     * @param value Float value to search
     * @return Array of addresses where value was found
     */
    external fun scanFloat(pid: Int, value: Float): LongArray?
    
    /**
     * Scan for double value in memory
     * @param pid Process ID
     * @param value Double value to search
     * @return Array of addresses where value was found
     */
    external fun scanDouble(pid: Int, value: Double): LongArray?
}