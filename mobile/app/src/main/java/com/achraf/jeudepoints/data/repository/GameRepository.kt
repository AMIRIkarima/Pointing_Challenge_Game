package com.achraf.jeudepoints.data.repository

import com.achraf.jeudepoints.data.model.GameDifficulty
import com.achraf.jeudepoints.data.model.GameSession
import com.achraf.jeudepoints.data.model.LeaderboardEntry
import com.achraf.jeudepoints.data.model.Player
import com.achraf.jeudepoints.data.model.PlayerLevel
import com.achraf.jeudepoints.data.remote.RetrofitClient
import com.achraf.jeudepoints.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository {

    private val apiService = RetrofitClient.apiService

    // ========== Player ==========

    suspend fun getPlayers(): Resource<List<Player>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getPlayers() }
            .fold(
                onSuccess = { response ->
                    if (response.isSuccessful && response.body() != null) {
                        Resource.Success(response.body()!!)
                    } else {
                        Resource.Error(response.message() ?: "Failed to fetch players")
                    }
                },
                onFailure = { Resource.Error(it.message ?: "Network error") }
            )
    }

    suspend fun ensurePlayer(username: String): Resource<Player> = withContext(Dispatchers.IO) {
        when (val playersResult = getPlayers()) {
            is Resource.Success -> {
                val existing = playersResult.data?.firstOrNull { it.username.equals(username, ignoreCase = true) }
                if (existing != null) {
                    Resource.Success(existing)
                } else {
                    createPlayer(username)
                }
            }
            is Resource.Error -> Resource.Error(playersResult.message ?: "Failed to fetch players")
            is Resource.Loading -> Resource.Loading()
        }
    }

    suspend fun createPlayer(username: String): Resource<Player> = withContext(Dispatchers.IO) {
        runCatching { apiService.createPlayer(mapOf("username" to username)) }
            .fold(
                onSuccess = { response ->
                    if (response.isSuccessful && response.body() != null) {
                        Resource.Success(response.body()!!)
                    } else {
                        Resource.Error(response.message() ?: "Failed to create player")
                    }
                },
                onFailure = { Resource.Error(it.message ?: "Network error") }
            )
    }

    suspend fun getPlayer(playerId: Long): Resource<Player> = withContext(Dispatchers.IO) {
        runCatching { apiService.getPlayer(playerId) }
            .fold(
                onSuccess = { response ->
                    if (response.isSuccessful && response.body() != null) {
                        Resource.Success(response.body()!!)
                    } else {
                        Resource.Error(response.message() ?: "Failed to fetch player")
                    }
                },
                onFailure = { Resource.Error(it.message ?: "Network error") }
            )
    }

    suspend fun getPlayerLevel(playerId: Long): Resource<PlayerLevel> = withContext(Dispatchers.IO) {
        runCatching { apiService.getPlayerLevel(playerId) }
            .fold(
                onSuccess = { response ->
                    if (response.isSuccessful && response.body() != null) {
                        Resource.Success(response.body()!!)
                    } else {
                        Resource.Error(response.message() ?: "Failed to fetch level")
                    }
                },
                onFailure = { Resource.Error(it.message ?: "Network error") }
            )
    }

    suspend fun updatePlayer(playerId: Long, username: String): Resource<Player> = withContext(Dispatchers.IO) {
        runCatching { apiService.updatePlayer(playerId, mapOf("id" to playerId, "username" to username)) }
            .fold(
                onSuccess = { response ->
                    if (response.isSuccessful && response.body() != null) {
                        Resource.Success(response.body()!!)
                    } else {
                        Resource.Error(response.message() ?: "Failed to update player")
                    }
                },
                onFailure = { Resource.Error(it.message ?: "Network error") }
            )
    }

    // ========== Game Sessions ==========

    suspend fun startGame(playerId: Long, difficulty: GameDifficulty): Resource<GameSession> =
        withContext(Dispatchers.IO) {
            runCatching { apiService.startGame(playerId, difficulty.name) }
                .fold(
                    onSuccess = { response ->
                        if (response.isSuccessful && response.body() != null) {
                            Resource.Success(response.body()!!)
                        } else {
                            Resource.Error(response.message() ?: "Failed to start game")
                        }
                    },
                    onFailure = { Resource.Error(it.message ?: "Network error") }
                )
        }

    suspend fun getActiveGame(playerId: Long): Resource<GameSession?> = withContext(Dispatchers.IO) {
        val playerResult = getPlayer(playerId)
        if (playerResult is Resource.Success && playerResult.data != null) {
            val latestGame = playerResult.data.games.maxByOrNull { it.id ?: 0L }
            Resource.Success(latestGame)
        } else if (playerResult is Resource.Error) {
            Resource.Error(playerResult.message ?: "Failed to fetch active game")
        } else {
            Resource.Error("Failed to fetch active game")
        }
    }

    suspend fun finishGame(gameId: Long, timeSeconds: Double, distance: Double): Resource<GameSession> =
        withContext(Dispatchers.IO) {
            runCatching { apiService.finishGame(gameId, mapOf("time_sec" to timeSeconds, "distance" to distance)) }
                .fold(
                    onSuccess = { response ->
                        if (response.isSuccessful && response.body() != null) {
                            Resource.Success(response.body()!!)
                        } else {
                            Resource.Error(response.message() ?: "Failed to finish game")
                        }
                    },
                    onFailure = { Resource.Error(it.message ?: "Network error") }
                )
        }

    suspend fun getPlayerHistory(playerId: Long): Resource<List<GameSession>> {
        // Backend doesn't expose a dedicated history endpoint; use embedded games on the player payload.
        val playerResult = getPlayer(playerId)
        return if (playerResult is Resource.Success && playerResult.data != null) {
            Resource.Success(playerResult.data.games.sortedByDescending { it.id ?: 0L })
        } else if (playerResult is Resource.Error) {
            Resource.Error(playerResult.message ?: "Failed to fetch history")
        } else {
            Resource.Error("History unavailable")
        }
    }

    // ========== Leaderboard ==========

    suspend fun getLeaderboard(limit: Int = 100): Resource<List<LeaderboardEntry>> {
        val playersResult = getPlayers()
        return if (playersResult is Resource.Success && playersResult.data != null) {
            val sorted = playersResult.data.sortedByDescending { it.totalScore }.take(limit)
            val entries = sorted.mapIndexed { index, player ->
                LeaderboardEntry(
                    rank = index + 1,
                    playerId = player.id ?: 0L,
                    username = player.username,
                    fullName = null,
                    totalScore = player.totalScore,
                    gamesPlayed = player.games.size,
                    averageScore = if (player.games.isNotEmpty())
                        player.games.mapNotNull { it.score }.average()
                    else 0.0
                )
            }
            Resource.Success(entries)
        } else if (playersResult is Resource.Error) {
            Resource.Error(playersResult.message ?: "Failed to fetch leaderboard")
        } else {
            Resource.Error("Leaderboard unavailable")
        }
    }

    suspend fun getPlayerRank(playerId: Long): Resource<LeaderboardEntry> {
        val leaderboardResult = getLeaderboard()
        return if (leaderboardResult is Resource.Success && leaderboardResult.data != null) {
            leaderboardResult.data.firstOrNull { it.playerId == playerId }?.let { Resource.Success(it) }
                ?: Resource.Error("Player not ranked")
        } else if (leaderboardResult is Resource.Error) {
            Resource.Error(leaderboardResult.message ?: "Failed to fetch rank")
        } else {
            Resource.Error("Rank unavailable")
        }
    }
}
