package com.arman.dev.gttool.data.model

data class ScanResult(
    val address: Long,
    val value: String,
    val valueType: ValueType,
    var isFrozen: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDisplayAddress(): String = "0x${address.toString(16).uppercase()}"
}