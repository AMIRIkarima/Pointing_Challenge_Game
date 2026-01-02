package com.achraf.jeudepoints.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achraf.jeudepoints.data.model.LeaderboardEntry
import com.achraf.jeudepoints.data.repository.GameRepository
import com.achraf.jeudepoints.utils.Resource
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _playerRank = MutableLiveData<Resource<LeaderboardEntry>>()
    val playerRank: LiveData<Resource<LeaderboardEntry>> = _playerRank

    fun loadPlayerRank(playerId: Long) {
        _playerRank.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.getPlayerRank(playerId)
            _playerRank.value = result
        }
    }
}
