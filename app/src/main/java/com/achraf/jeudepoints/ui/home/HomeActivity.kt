package com.achraf.jeudepoints.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.achraf.jeudepoints.R
import com.achraf.jeudepoints.databinding.ActivityHomeBinding
import com.achraf.jeudepoints.ui.game.GameActivity
import com.achraf.jeudepoints.ui.history.HistoryActivity
import com.achraf.jeudepoints.ui.leaderboard.LeaderboardActivity
import com.achraf.jeudepoints.ui.profile.ProfileActivity
import com.achraf.jeudepoints.utils.Resource
import com.achraf.jeudepoints.utils.SessionManager
import com.achraf.jeudepoints.MainActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        setupToolbar()
        setupBottomNavigation()
        setupListeners()
        observeViewModel()

        // Load data
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    false
                }
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    false
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.btnStartGame.setOnClickListener {
            showDifficultyDialog()
        }

        binding.btnViewGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.player.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { player ->
                        binding.tvUsername.text = player.username
                        binding.tvGamesPlayed.text = player.games.size.toString()
                    }
                }
                is Resource.Error -> {
                    // Use username from session if API fails
                    binding.tvUsername.text = sessionManager.getUsername() ?: "User"
                    Toast.makeText(this, result.message ?: "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Could show loading indicator
                }
            }
        }

        viewModel.activeGame.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data != null) {
                        // There's an active game
                        binding.cardActiveGame.visibility = View.VISIBLE
                        binding.tvGameStatus.text = result.data.status.displayName
                        binding.tvGameDifficulty.text = result.data.difficulty.displayName

                        // Set status color
                        val statusColor = when {
                            result.data.isActive() -> R.color.game_in_progress
                            result.data.isFinished() -> R.color.game_completed
                            else -> R.color.grey
                        }
                        binding.tvGameStatus.setTextColor(getColor(statusColor))
                    } else {
                        // No active game
                        binding.cardActiveGame.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    binding.cardActiveGame.visibility = View.GONE
                }
                is Resource.Loading -> {
                    // Could show loading
                }
            }
        }
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()

        viewModel.refreshData(userId)
    }

    private fun showDifficultyDialog() {
        val difficulties = arrayOf("Easy", "Medium", "Hard")

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_difficulty))
            .setItems(difficulties) { _, which ->
                val selectedDifficulty = difficulties[which]
                navigateToGame(selectedDifficulty)
            }
            .show()
    }

    private fun navigateToGame(difficulty: String) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("DIFFICULTY", difficulty)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            R.id.action_refresh -> {
                loadData()
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        sessionManager.clearSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }



    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        loadData() // Refresh data when returning to home
    }
}
