package com.arman.dev.gttool.presentation.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arman.dev.gttool.data.repository.GameRepository
import com.arman.dev.gttool.domain.usecase.GetInstalledGamesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    
    private val gameRepository = GameRepository(application)
    private val getInstalledGamesUseCase = GetInstalledGamesUseCase(gameRepository)
    
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()
    
    init {
        loadGames()
    }
    
    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            MainEvent.LoadGames -> loadGames()
        }
    }
    
    private fun loadGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            getInstalledGamesUseCase().onSuccess { games ->
                _state.update {
                    it.copy(
                        games = games,
                        isLoading = false
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

sealed class MainEvent {
    data class SearchQueryChanged(val query: String) : MainEvent()
    object LoadGames : MainEvent()
}