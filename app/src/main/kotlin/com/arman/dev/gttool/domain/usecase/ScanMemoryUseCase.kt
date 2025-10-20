package com.arman.dev.gttool.domain.usecase

import com.arman.dev.gttool.data.model.ScanResult
import com.arman.dev.gttool.data.model.ScanType
import com.arman.dev.gttool.data.model.ValueType
import com.arman.dev.gttool.data.repository.MemoryRepository

class ScanMemoryUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(
        pid: Int,
        searchValue: String,
        valueType: ValueType,
        scanType: ScanType
    ): Result<List<ScanResult>> {
        return repository.scanMemory(pid, searchValue, valueType, scanType)
    }
    
    suspend fun refineScan(
        searchValue: String,
        scanType: ScanType
    ): Result<List<ScanResult>> {
        return repository.refineScan(searchValue, scanType)
    }
    
    fun getLastResults(): List<ScanResult> {
        return repository.getLastResults()
    }
    
    fun clearResults() {
        repository.clearResults()
    }
}