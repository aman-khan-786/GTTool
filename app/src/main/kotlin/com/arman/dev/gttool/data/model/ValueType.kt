package com.arman.dev.gttool.data.model

enum class ValueType(val size: Int, val displayName: String) {
    BYTE(1, "Byte"),
    WORD(2, "Word"),
    DWORD(4, "Dword"),
    QWORD(8, "Qword"),
    FLOAT(4, "Float"),
    DOUBLE(8, "Double"),
    AOB(0, "Array of Bytes"),  // New: For hex byte search (e.g., memory scan)
    UTF8(0, "UTF-8 String");   // New: For string values in game data

    companion object {
        fun fromDisplayName(name: String): ValueType? = values().find { it.displayName == name }
        fun getSizeForHack(type: ValueType, value: Any): Int = if (type.size > 0) type.size else value.toString().length  // Dynamic size for AOB/UTF8
    }
}
