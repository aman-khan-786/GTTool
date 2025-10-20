package com.arman.dev.gttool.presentation.main

import com.arman.dev.gttool.data.model.Game

data class MainState(
    val games: List<Game> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)