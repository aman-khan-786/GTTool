package com.arman.dev.gttool.presentation.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arman.dev.gttool.data.model.ScanResult
import com.arman.dev.gttool.data.model.ScanType
import com.arman.dev.gttool.data.model.ValueType
import com.arman.dev.gttool.data.repository.MemoryRepository
import com.arman.dev.gttool.domain.usecase.ModifyMemoryUseCase
import com.arman.dev.gttool.domain.usecase.MonitorMemoryUseCase
import com.arman.dev.gttool.domain.usecase.ScanMemoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OverlayViewModel : ViewModel() {
    
    // Repository - No constructor parameter
    private val memoryRepository = MemoryRepository()
    
    // Use cases
    private val scanMemoryUseCase = ScanMemoryUseCase(memoryRepository)
    private val modifyMemoryUseCase = ModifyMemoryUseCase(memoryRepository)
    private val monitorMemoryUseCase = MonitorMemoryUseCase(memoryRepository)
    
    // State
    private val _state = MutableStateFlow(OverlayState())
    val state: StateFlow<OverlayState> = _state.asStateFlow()
    
    fun onEvent(event: OverlayEvent) {
        when (event) {
            is OverlayEvent.ToggleMinimize -> toggleMinimize()
            is OverlayEvent.SwitchTab -> switchTab(event.tab)
            is OverlayEvent.StartScan -> startScan(event.pid, event.searchValue, event.valueType)
            is OverlayEvent.RefineScan -> refineScan(event.searchValue)
            is OverlayEvent.ModifyValue -> modifyValue(event.address, event.newValue)
            is OverlayEvent.FreezeValue -> freezeValue(event.address, event.value)
            is OverlayEvent.UnfreezeValue -> unfreezeValue(event.address)
            is OverlayEvent.ClearResults -> clearResults()
            is OverlayEvent.StartMonitoring -> startMonitoring(event.pid)
            is OverlayEvent.StopMonitoring -> stopMonitoring()
        }
    }
    
    private fun toggleMinimize() {
        _state.update { it.copy(isMinimized = !it.isMinimized) }
    }
    
    private fun switchTab(tab: OverlayTab) {
        _state.update { it.copy(currentTab = tab) }
    }
    
    private fun startScan(pid: Int, searchValue: String, valueType: ValueType) {
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, scanError = null) }
            
            val result = scanMemoryUseCase(
                pid = pid,
                searchValue = searchValue,
                valueType = valueType,
                scanType = ScanType.EXACT
            )
            
            result.onSuccess { results ->
                _state.update {
                    it.copy(
                        scanResults = results,
                        isScanning = false
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isScanning = false,
                        scanError = error.message
                    )
                }
            }
        }
    }
    
    private fun refineScan(searchValue: String) {
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true) }
            
            val result = scanMemoryUseCase.refineScan(
                searchValue = searchValue,
                scanType = ScanType.EXACT
            )
            
            result.onSuccess { results ->
                _state.update {
                    it.copy(
                        scanResults = results,
                        isScanning = false
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isScanning = false,
                        scanError = error.message
                    )
                }
            }
        }
    }
    
    private fun modifyValue(address: Long, newValue: String) {
        viewModelScope.launch {
            val currentValueType = _state.value.scanResults
                .find { it.address == address }?.valueType ?: ValueType.DWORD
            
            val result = modifyMemoryUseCase(
                address = address,
                newValue = newValue,
                valueType = currentValueType
            )
            
            result.onSuccess { success ->
                if (success) {
                    // Update UI or show success message
                }
            }
        }
    }
    
    private fun freezeValue(address: Long, value: String) {
        viewModelScope.launch {
            val currentValueType = _state.value.scanResults
                .find { it.address == address }?.valueType ?: ValueType.DWORD
            
            val result = monitorMemoryUseCase.freezeValue(
                address = address,
                value = value,
                valueType = currentValueType
            )
            
            result.onSuccess { success ->
                if (success) {
                    // Update frozen status in scan results
                    val updatedResults = _state.value.scanResults.map {
                        if (it.address == address) it.copy(isFrozen = true) else it
                    }
                    _state.update { it.copy(scanResults = updatedResults) }
                }
            }
        }
    }
    
    private fun unfreezeValue(address: Long) {
        viewModelScope.launch {
            val result = monitorMemoryUseCase.unfreezeValue(address)
            
            result.onSuccess { success ->
                if (success) {
                    // Update frozen status
                    val updatedResults = _state.value.scanResults.map {
                        if (it.address == address) it.copy(isFrozen = false) else it
                    }
                    _state.update { it.copy(scanResults = updatedResults) }
                }
            }
        }
    }
    
    private fun clearResults() {
        scanMemoryUseCase.clearResults()
        _state.update { it.copy(scanResults = emptyList()) }
    }
    
    private fun startMonitoring(pid: Int) {
        viewModelScope.launch {
            val addresses = _state.value.scanResults.associate {
                it.address to it.valueType
            }
            
            monitorMemoryUseCase.monitorAddresses(pid, addresses)
                .collect { values ->
                    _state.update { it.copy(monitoredAddresses = values) }
                }
        }
    }
    
    private fun stopMonitoring() {
        _state.update { it.copy(monitoredAddresses = emptyMap()) }
    }
}

// State
data class OverlayState(
    val isMinimized: Boolean = false,
    val currentTab: OverlayTab = OverlayTab.SCANNER,
    val scanResults: List<ScanResult> = emptyList(),
    val isScanning: Boolean = false,
    val scanError: String? = null,
    val monitoredAddresses: Map<Long, String> = emptyMap()
)

// Events
sealed class OverlayEvent {
    object ToggleMinimize : OverlayEvent()
    data class SwitchTab(val tab: OverlayTab) : OverlayEvent()
    data class StartScan(val pid: Int, val searchValue: String, val valueType: ValueType) : OverlayEvent()
    data class RefineScan(val searchValue: String) : OverlayEvent()
    data class ModifyValue(val address: Long, val newValue: String) : OverlayEvent()
    data class FreezeValue(val address: Long, val value: String) : OverlayEvent()
    data class UnfreezeValue(val address: Long) : OverlayEvent()
    object ClearResults : OverlayEvent()
    data class StartMonitoring(val pid: Int) : OverlayEvent()
    object StopMonitoring : OverlayEvent()
}

// Tabs
enum class OverlayTab {
    SCANNER,
    EDITOR,
    MONITOR
}