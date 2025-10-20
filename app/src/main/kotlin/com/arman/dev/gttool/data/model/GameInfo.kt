package com.arman.dev.gttool.data.model

data class Game(
    val packageName: String,
    val name: String,
    val version: String,
    val icon: Any? = null
)