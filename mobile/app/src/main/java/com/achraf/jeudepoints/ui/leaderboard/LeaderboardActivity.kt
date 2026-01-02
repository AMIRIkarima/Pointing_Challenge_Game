package com.achraf.jeudepoints.ui.leaderboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.achraf.jeudepoints.R
import com.achraf.jeudepoints.databinding.ActivityLeaderboardBinding
import com.achraf.jeudepoints.ui.history.HistoryActivity
import com.achraf.jeudepoints.ui.home.HomeActivity
import com.achraf.jeudepoints.ui.profile.ProfileActivity
import com.achraf.jeudepoints.utils.Resource
import com.achraf.jeudepoints.utils.SessionManager

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var viewModel: LeaderboardViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[LeaderboardViewModel::class.java]

        setupToolbar()
        setupBottomNavigation()
        setupSwipeRefresh()
        observeViewModel()

        loadPlayerRank()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_leaderboard
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
                R.id.nav_leaderboard -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadPlayerRank()
        }
    }

    private fun observeViewModel() {
        viewModel.playerRank.observe(this) { result ->
            binding.swipeRefresh.isRefreshing = false

            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.let { playerEntry ->
                        binding.tvEmptyState.visibility = View.GONE
                        binding.cardUserRank.visibility = View.VISIBLE

                        binding.tvUserRank.text = "#${playerEntry.rank}"
                        binding.tvUserGames.text = "${playerEntry.gamesPlayed}"
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.cardUserRank.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    Toast.makeText(this, result.message ?: "Failed to load ranking", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadPlayerRank() {
        val playerId = sessionManager.getUserId()
        viewModel.loadPlayerRank(playerId)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_leaderboard
    }
}
