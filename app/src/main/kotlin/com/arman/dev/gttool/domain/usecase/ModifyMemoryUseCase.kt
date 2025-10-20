package com.arman.dev.gttool.domain.usecase

import com.arman.dev.gttool.data.model.ValueType
import com.arman.dev.gttool.data.repository.MemoryRepository

class ModifyMemoryUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(
        address: Long,
        newValue: String,
        valueType: ValueType
    ): Result<Boolean> {
        return repository.modifyValue(address, newValue, valueType)
    }
    
    suspend fun readValue(
        pid: Int,
        address: Long,
        valueType: ValueType
    ): Result<String> {
        return repository.readMemoryValue(pid, address, valueType)
    }
}