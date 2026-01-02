package com.achraf.jeudepoints.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achraf.jeudepoints.data.model.AuthResponse
import com.achraf.jeudepoints.data.repository.GameRepository
import com.achraf.jeudepoints.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _loginResult = MutableLiveData<Resource<AuthResponse>>()
    val loginResult: LiveData<Resource<AuthResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Resource<AuthResponse>>()
    val registerResult: LiveData<Resource<AuthResponse>> = _registerResult

    fun login(username: String, password: String) {
        if (username.isBlank()) {
            _loginResult.value = Resource.Error("Username cannot be empty")
            return
        }

        _loginResult.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.ensurePlayer(username)
            _loginResult.value = when (result) {
                is Resource.Success -> result.data?.let { player ->
                    Resource.Success(AuthResponse(token = "local_session", player = player, message = "Login successful"))
                } ?: Resource.Error("No player returned")
                is Resource.Error -> Resource.Error(result.message ?: "Login failed")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }

    fun register(username: String, email: String, password: String, fullName: String?) {
        if (username.isBlank()) {
            _registerResult.value = Resource.Error("Username cannot be empty")
            return
        }

        _registerResult.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.createPlayer(username)
            _registerResult.value = when (result) {
                is Resource.Success -> result.data?.let { player ->
                    Resource.Success(AuthResponse(token = "local_session", player = player, message = "Registration successful"))
                } ?: Resource.Error("No player returned")
                is Resource.Error -> Resource.Error(result.message ?: "Registration failed")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }
}
