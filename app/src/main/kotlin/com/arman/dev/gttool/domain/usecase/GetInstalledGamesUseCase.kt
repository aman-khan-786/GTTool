package com.arman.dev.gttool.domain.usecase

import com.arman.dev.gttool.data.model.Game
import com.arman.dev.gttool.data.repository.GameRepository

class GetInstalledGamesUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(): Result<List<Game>> {
        return try {
            Result.success(repository.getInstalledGames())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}