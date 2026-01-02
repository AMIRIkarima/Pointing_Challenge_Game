package com.achraf.jeudepoints

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.achraf.jeudepoints.databinding.ActivityMainBinding
import com.achraf.jeudepoints.data.repository.GameRepository
import com.achraf.jeudepoints.ui.home.HomeActivity
import com.achraf.jeudepoints.utils.Resource
import com.achraf.jeudepoints.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private val repository = GameRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // If user already has a username, go directly to home
        if (sessionManager.getUsername() != null) {
            navigateToHome()
            return
        }

        // Show the username entry screen
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnStart.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()

            if (username.isEmpty()) {
                binding.tilUsername.error = "Please enter a username"
                return@setOnClickListener
            }

            if (username.length < 3) {
                binding.tilUsername.error = "Username must be at least 3 characters"
                return@setOnClickListener
            }

            // Clear any previous error
            binding.tilUsername.error = null

            binding.btnStart.isEnabled = false
            lifecycleScope.launch {
                when (val result = repository.ensurePlayer(username)) {
                    is Resource.Success -> {
                        val player = result.data
                        if (player != null) {
                            sessionManager.saveUsername(player.username)
                            player.id?.let { sessionManager.saveUserId(it) }
                            sessionManager.saveAuthToken("local_session")
                            Toast.makeText(this@MainActivity, "Welcome, ${player.username}!", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        } else {
                            Toast.makeText(this@MainActivity, "Unable to load player", Toast.LENGTH_SHORT).show()
                            binding.btnStart.isEnabled = true
                        }
                    }
                    is Resource.Error -> {
                        binding.btnStart.isEnabled = true
                        Toast.makeText(this@MainActivity, result.message ?: "Failed to connect to backend", Toast.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> Unit
                }
            }
        }

        // Clear error when user starts typing
        binding.etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilUsername.error = null
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
