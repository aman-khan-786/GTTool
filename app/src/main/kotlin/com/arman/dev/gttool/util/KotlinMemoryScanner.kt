package com.arman.dev.gttool.util

import com.arman.dev.gttool.data.model.ValueType
import java.io.File
import java.io.RandomAccessFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object KotlinMemoryScanner {
    
    suspend fun scanMemory(
        pid: Int,
        searchValue: Long,
        valueType: ValueType
    ): List<MemoryAddress> = withContext(Dispatchers.IO) {
        val results = mutableListOf<MemoryAddress>()
        
        try {
            val regions = getMemoryRegions(pid)
            
            for (region in regions) {
                if (region.isReadable && region.size < 50 * 1024 * 1024) {
                    val regionResults = scanRegion(pid, region, searchValue, valueType)
                    results.addAll(regionResults)
                    
                    if (results.size > 10000) break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        results
    }
    
    private fun scanRegion(
        pid: Int,
        region: MemoryRegion,
        searchValue: Long,
        valueType: ValueType
    ): List<MemoryAddress> {
        val results = mutableListOf<MemoryAddress>()
        val memFile = File("/proc/$pid/mem")
        
        if (!memFile.exists() || !memFile.canRead()) {
            return results
        }
        
        try {
            RandomAccessFile(memFile, "r").use { raf ->
                val size = minOf(region.size, 10 * 1024 * 1024).toInt()
                val buffer = ByteArray(size)
                
                raf.seek(region.start)
                
                val bytesRead = try {
                    raf.read(buffer)
                } catch (e: Exception) {
                    return results
                }
                
                if (bytesRead <= 0) return results
                
                val step = valueType.size
                for (i in 0 until bytesRead - step step step) {
                    val value = readValue(buffer, i, valueType)
                    
                    if (value == searchValue) {
                        results.add(
                            MemoryAddress(
                                address = region.start + i,
                                value = value,
                                valueType = valueType
                            )
                        )
                        
                        if (results.size > 1000) break
                    }
                }
            }
        } catch (e: Exception) {
            // Silent
        }
        
        return results
    }
    
    private fun readValue(buffer: ByteArray, offset: Int, valueType: ValueType): Long {
        return when (valueType) {
            ValueType.BYTE -> buffer[offset].toLong()
            ValueType.WORD -> readShort(buffer, offset).toLong()
            ValueType.DWORD -> readInt(buffer, offset).toLong()
            ValueType.QWORD -> readLong(buffer, offset)
            ValueType.FLOAT -> Float.fromBits(readInt(buffer, offset)).toLong()
            ValueType.DOUBLE -> Double.fromBits(readLong(buffer, offset)).toLong()
        }
    }
    
    suspend fun writeMemory(
        pid: Int,
        address: Long,
        value: Long,
        valueType: ValueType
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val memFile = File("/proc/$pid/mem")
            
            RandomAccessFile(memFile, "rw").use { raf ->
                raf.seek(address)
                
                when (valueType) {
                    ValueType.BYTE -> raf.writeByte(value.toInt())
                    ValueType.WORD -> raf.writeShort(value.toInt())
                    ValueType.DWORD -> raf.writeInt(value.toInt())
                    ValueType.QWORD -> raf.writeLong(value)
                    ValueType.FLOAT -> raf.writeFloat(value.toFloat())
                    ValueType.DOUBLE -> raf.writeDouble(value.toDouble())
                }
                
                return@withContext true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun readMemory(
        pid: Int,
        address: Long,
        valueType: ValueType
    ): Long? = withContext(Dispatchers.IO) {
        try {
            val memFile = File("/proc/$pid/mem")
            
            RandomAccessFile(memFile, "r").use { raf ->
                raf.seek(address)
                
                return@withContext when (valueType) {
                    ValueType.BYTE -> raf.readByte().toLong()
                    ValueType.WORD -> raf.readShort().toLong()
                    ValueType.DWORD -> raf.readInt().toLong()
                    ValueType.QWORD -> raf.readLong()
                    ValueType.FLOAT -> raf.readFloat().toLong()
                    ValueType.DOUBLE -> raf.readDouble().toLong()
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getMemoryRegions(pid: Int): List<MemoryRegion> {
        val regions = mutableListOf<MemoryRegion>()
        
        try {
            File("/proc/$pid/maps").forEachLine { line ->
                val parts = line.split(Regex("\\s+"))
                if (parts.size >= 2) {
                    val addresses = parts[0].split("-")
                    if (addresses.size == 2) {
                        val start = addresses[0].toLong(16)
                        val end = addresses[1].toLong(16)
                        val perms = parts[1]
                        
                        val isGoodRegion = line.contains("[heap]") || 
                                          line.contains("[anon") ||
                                          !line.contains("[")
                        
                        if (isGoodRegion) {
                            regions.add(
                                MemoryRegion(
                                    start = start,
                                    end = end,
                                    size = end - start,
                                    isReadable = perms.contains("r"),
                                    isWritable = perms.contains("w")
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return regions
    }
    
    private fun readShort(buffer: ByteArray, offset: Int): Short {
        return ((buffer[offset + 1].toInt() and 0xFF) shl 8 or
                (buffer[offset].toInt() and 0xFF)).toShort()
    }
    
    private fun readInt(buffer: ByteArray, offset: Int): Int {
        return ((buffer[offset + 3].toInt() and 0xFF) shl 24) or
                ((buffer[offset + 2].toInt() and 0xFF) shl 16) or
                ((buffer[offset + 1].toInt() and 0xFF) shl 8) or
                (buffer[offset].toInt() and 0xFF)
    }
    
    private fun readLong(buffer: ByteArray, offset: Int): Long {
        var value = 0L
        for (i in 0 until 8) {
            value = value or ((buffer[offset + i].toLong() and 0xFF) shl (i * 8))
        }
        return value
    }
}

data class MemoryAddress(
    val address: Long,
    val value: Long,
    val valueType: com.arman.dev.gttool.data.model.ValueType
)

data class MemoryRegion(
    val start: Long,
    val end: Long,
    val size: Long,
    val isReadable: Boolean,
    val isWritable: Boolean
)