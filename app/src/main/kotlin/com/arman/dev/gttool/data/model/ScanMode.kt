package com.arman.dev.gttool.data.model

enum class ScanMode(val displayName: String) {
    EXACT("Exact Value"),
    FUZZY("Fuzzy Search"),
    INCREASED("Increased"),
    DECREASED("Decreased"),
    CHANGED("Changed"),
    UNCHANGED("Unchanged"),
    UNKNOWN("Unknown Initial Value");
    
    fun requiresValue(): Boolean {
        return this == EXACT || this == FUZZY
    }
}