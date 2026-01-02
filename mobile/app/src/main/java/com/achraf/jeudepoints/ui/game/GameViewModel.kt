package com.achraf.jeudepoints.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achraf.jeudepoints.data.model.GameDifficulty
import com.achraf.jeudepoints.data.model.GameSession
import com.achraf.jeudepoints.data.repository.GameRepository
import com.achraf.jeudepoints.utils.Resource
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _currentGame = MutableLiveData<Resource<GameSession>>()
    val currentGame: LiveData<Resource<GameSession>> = _currentGame

    private val _gameStarted = MutableLiveData<Resource<GameSession>>()
    val gameStarted: LiveData<Resource<GameSession>> = _gameStarted

    fun startGame(playerId: Long, difficulty: GameDifficulty) {
        _gameStarted.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.startGame(playerId, difficulty)
            _gameStarted.value = result

            // Also update current game
            if (result is Resource.Success) {
                _currentGame.value = result
            }
        }
    }

    fun loadActiveGame(playerId: Long) {
        _currentGame.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.getActiveGame(playerId)
            _currentGame.value = if (result is Resource.Success && result.data != null) {
                Resource.Success(result.data)
            } else {
                Resource.Error("No active game")
            }
        }
    }

    fun updateGameState(gameSession: GameSession) {
        _currentGame.value = Resource.Success(gameSession)
    }
}
