package com.arman.dev.gttool.data.repository

import com.arman.dev.gttool.data.model.ScanResult
import com.arman.dev.gttool.data.model.ScanType
import com.arman.dev.gttool.data.model.ValueType
import com.arman.dev.gttool.util.KotlinMemoryScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemoryRepository {
    
    private var lastResults = listOf<ScanResult>()
    private var currentPid: Int = 0
    
    // REAL Memory Scan - Using KotlinMemoryScanner
    suspend fun scanMemory(
        pid: Int,
        searchValue: String,
        valueType: ValueType,
        scanType: ScanType
    ): Result<List<ScanResult>> = withContext(Dispatchers.IO) {
        try {
            currentPid = pid
            
            // Parse search value
            val numValue = when (valueType) {
                ValueType.FLOAT, ValueType.DOUBLE -> {
                    searchValue.toDoubleOrNull()?.toLong() ?: 0L
                }
                else -> searchValue.toLongOrNull() ?: 0L
            }
            
            // REAL SCAN - KotlinMemoryScanner
            val memoryAddresses = KotlinMemoryScanner.scanMemory(
                pid = pid,
                searchValue = numValue,
                valueType = valueType
            )
            
            // Convert to ScanResult
            val results = memoryAddresses.map { addr ->
                ScanResult(
                    address = addr.address,
                    value = addr.value.toString(),
                    valueType = valueType,
                    isFrozen = false
                )
            }
            
            lastResults = results
            Result.success(results)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Refine Scan - Scan only previous results
    suspend fun refineScan(
        searchValue: String,
        scanType: ScanType
    ): Result<List<ScanResult>> = withContext(Dispatchers.IO) {
        try {
            if (lastResults.isEmpty()) {
                return@withContext Result.success(emptyList())
            }
            
            val numValue = searchValue.toLongOrNull() ?: 0L
            
            // Re-read values at previous addresses
            val refined = lastResults.mapNotNull { result ->
                val currentValue = KotlinMemoryScanner.readMemory(
                    pid = currentPid,
                    address = result.address,
                    valueType = result.valueType
                )
                
                if (currentValue != null && currentValue == numValue) {
                    result.copy(value = currentValue.toString())
                } else {
                    null
                }
            }
            
            lastResults = refined
            Result.success(refined)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // REAL Modify Value - Write to memory
    suspend fun modifyValue(
        address: Long,
        newValue: String,
        valueType: ValueType
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val numValue = when (valueType) {
                ValueType.FLOAT, ValueType.DOUBLE -> {
                    newValue.toDoubleOrNull()?.toLong() ?: 0L
                }
                else -> newValue.toLongOrNull() ?: 0L
            }
            
            // REAL WRITE - KotlinMemoryScanner
            val success = KotlinMemoryScanner.writeMemory(
                pid = currentPid,
                address = address,
                value = numValue,
                valueType = valueType
            )
            
            Result.success(success)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Read current memory value
    suspend fun readMemoryValue(
        pid: Int,
        address: Long,
        valueType: ValueType
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // REAL READ
            val value = KotlinMemoryScanner.readMemory(
                pid = pid,
                address = address,
                valueType = valueType
            )
            
            Result.success(value?.toString() ?: "0")
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Freeze value - Keep writing same value
    suspend fun freezeValue(
        address: Long,
        value: String,
        valueType: ValueType
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Mark as frozen in results
            lastResults = lastResults.map {
                if (it.address == address) it.copy(isFrozen = true) else it
            }
            
            // TODO: Start background service to keep writing
            Result.success(true)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Unfreeze value
    suspend fun unfreezeValue(address: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            lastResults = lastResults.map {
                if (it.address == address) it.copy(isFrozen = false) else it
            }
            
            Result.success(true)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getLastResults(): List<ScanResult> = lastResults
    
    fun clearResults() {
        lastResults = emptyList()
    }
}