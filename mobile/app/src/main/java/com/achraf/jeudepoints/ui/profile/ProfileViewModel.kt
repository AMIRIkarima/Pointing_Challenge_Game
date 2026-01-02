package com.achraf.jeudepoints.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achraf.jeudepoints.data.model.Player
import com.achraf.jeudepoints.data.repository.GameRepository
import com.achraf.jeudepoints.utils.Resource
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _player = MutableLiveData<Resource<Player>>()
    val player: LiveData<Resource<Player>> = _player

    private val _updateResult = MutableLiveData<Resource<Player>>()
    val updateResult: LiveData<Resource<Player>> = _updateResult

    fun loadPlayer(playerId: Long) {
        _player.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.getPlayer(playerId)
            _player.value = result
        }
    }

    fun updatePlayer(playerId: Long, username: String) {
        _updateResult.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.updatePlayer(playerId, username)
            _updateResult.value = result

            if (result is Resource.Success) {
                _player.value = result
            }
        }
    }
}
