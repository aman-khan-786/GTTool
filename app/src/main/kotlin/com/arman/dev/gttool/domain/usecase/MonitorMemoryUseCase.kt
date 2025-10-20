package com.arman.dev.gttool.domain.usecase

import com.arman.dev.gttool.data.model.ValueType
import com.arman.dev.gttool.data.repository.MemoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MonitorMemoryUseCase(
    private val repository: MemoryRepository
) {
    suspend fun freezeValue(
        address: Long,
        value: String,
        valueType: ValueType
    ): Result<Boolean> {
        return repository.freezeValue(address, value, valueType)
    }
    
    suspend fun unfreezeValue(address: Long): Result<Boolean> {
        return repository.unfreezeValue(address)
    }
    
    fun monitorAddresses(
        pid: Int,
        addresses: Map<Long, ValueType>
    ): Flow<Map<Long, String>> = flow {
        while (true) {
            val currentValues = mutableMapOf<Long, String>()
            
            addresses.forEach { (address, valueType) ->
                val result = repository.readMemoryValue(pid, address, valueType)
                result.onSuccess { value ->
                    currentValues[address] = value
                }
            }
            
            emit(currentValues)
            delay(1000) // Update every second
        }
    }
}