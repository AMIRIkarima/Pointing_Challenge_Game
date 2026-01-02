package com.achraf.jeudepoints.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achraf.jeudepoints.data.model.GameSession
import com.achraf.jeudepoints.data.model.Player
import com.achraf.jeudepoints.data.repository.GameRepository
import com.achraf.jeudepoints.utils.Resource
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _player = MutableLiveData<Resource<Player>>()
    val player: LiveData<Resource<Player>> = _player

    private val _activeGame = MutableLiveData<Resource<GameSession?>>()
    val activeGame: LiveData<Resource<GameSession?>> = _activeGame

    fun loadPlayerData(playerId: Long) {
        _player.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.getPlayer(playerId)
            _player.value = result
        }
    }

    fun checkActiveGame(playerId: Long) {
        _activeGame.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.getActiveGame(playerId)
            _activeGame.value = result
        }
    }

    fun refreshData(playerId: Long) {
        loadPlayerData(playerId)
        checkActiveGame(playerId)
    }
}
