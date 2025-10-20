package com.arman.dev.gttool.presentation.scanner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScannerViewModel : ViewModel() {
    
    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()
    
    fun updateProgress(progress: Float) {
        _scanProgress.value = progress
    }
    
    fun resetProgress() {
        _scanProgress.value = 0f
    }
}