package com.arman.dev.gttool.data.model

enum class ValueType(val size: Int, val displayName: String) {
    BYTE(1, "Byte"),
    WORD(2, "Word"),
    DWORD(4, "Dword"),
    QWORD(8, "Qword"),
    FLOAT(4, "Float"),
    DOUBLE(8, "Double")
}