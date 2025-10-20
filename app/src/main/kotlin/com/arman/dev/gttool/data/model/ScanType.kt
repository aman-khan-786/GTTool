package com.arman.dev.gttool.data.model

enum class ScanType {
    EXACT,           // Exact value search
    FUZZY,           // Unknown value search
    INCREASED,       // Value increased
    DECREASED,       // Value decreased
    CHANGED,         // Value changed
    UNCHANGED        // Value unchanged
}