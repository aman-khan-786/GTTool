package com.arman.dev.gttool.util

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Long.toHexString(): String {
    return "0x${this.toString(16).uppercase()}"
}

fun String.isValidNumber(): Boolean {
    return this.toIntOrNull() != null || this.toLongOrNull() != null || 
           this.toFloatOrNull() != null || this.toDoubleOrNull() != null
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

fun Int.toByteArray(): ByteArray {
    return byteArrayOf(
        (this and 0xFF).toByte(),
        (this shr 8 and 0xFF).toByte(),
        (this shr 16 and 0xFF).toByte(),
        (this shr 24 and 0xFF).toByte()
    )
}

fun Long.toByteArray(): ByteArray {
    return ByteArray(8) { i -> (this shr (i * 8) and 0xFF).toByte() }
}