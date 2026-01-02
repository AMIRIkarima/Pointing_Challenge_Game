package com.achraf.jeudepoints.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achraf.jeudepoints.data.model.GameSession
import com.achraf.jeudepoints.data.repository.GameRepository
import com.achraf.jeudepoints.utils.Resource
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _gameHistory = MutableLiveData<Resource<List<GameSession>>>()
    val gameHistory: LiveData<Resource<List<GameSession>>> = _gameHistory

    fun loadHistory(playerId: Long) {
        _gameHistory.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.getPlayerHistory(playerId)
            _gameHistory.value = result
        }
    }
}
