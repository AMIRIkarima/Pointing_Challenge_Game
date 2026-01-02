package com.achraf.jeudepoints.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.achraf.jeudepoints.R
import com.achraf.jeudepoints.data.model.Player
import com.achraf.jeudepoints.databinding.ActivityProfileBinding
import com.achraf.jeudepoints.ui.game.GameActivity
import com.achraf.jeudepoints.ui.history.HistoryActivity
import com.achraf.jeudepoints.ui.home.HomeActivity
import com.achraf.jeudepoints.ui.leaderboard.LeaderboardActivity
import com.achraf.jeudepoints.utils.Resource
import com.achraf.jeudepoints.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sessionManager: SessionManager

    private var currentPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        setupToolbar()
        setupBottomNavigation()
        setupListeners()
        observeViewModel()

        loadPlayerData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun observeViewModel() {
        viewModel.player.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.let { player ->
                        currentPlayer = player
                        displayPlayerData(player)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message ?: "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.updateResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message ?: "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadPlayerData() {
        val userId = sessionManager.getUserId()
        viewModel.loadPlayer(userId)
    }

    private fun displayPlayerData(player: Player) {
        binding.etUsername.setText(player.username)
        binding.etEmail.setText("") // Backend does not manage email; keep field empty
        binding.etFullName.setText("") // Backend does not manage full name

        binding.tvTotalScore.text = player.totalScore.toString()
        binding.tvGamesPlayed.text = player.games.size.toString()

        val avgScore = if (player.games.isNotEmpty()) {
            player.games.mapNotNull { it.score }.average()
        } else {
            0.0
        }
        binding.tvAverageScore.text = String.format("%.1f", avgScore)
    }

    private fun saveProfile() {
        val newUsername = binding.etUsername.text.toString().ifBlank { currentPlayer?.username ?: "" }
        val userId = sessionManager.getUserId()
        viewModel.updatePlayer(userId, newUsername)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
    }
}
