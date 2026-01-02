package com.achraf.jeudepoints.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.achraf.jeudepoints.R
import com.achraf.jeudepoints.databinding.ActivityHistoryBinding
import com.achraf.jeudepoints.ui.home.HomeActivity
import com.achraf.jeudepoints.ui.leaderboard.LeaderboardActivity
import com.achraf.jeudepoints.ui.profile.ProfileActivity
import com.achraf.jeudepoints.utils.Resource
import com.achraf.jeudepoints.utils.SessionManager

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: GameHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        setupToolbar()
        setupBottomNavigation()
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        loadHistory()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_history
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_history -> true
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = GameHistoryAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadHistory()
        }
    }

    private fun observeViewModel() {
        viewModel.gameHistory.observe(this) { result ->
            binding.swipeRefresh.isRefreshing = false

            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data?.let { games ->
                        if (games.isEmpty()) {
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            adapter.updateGames(games)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message ?: "Failed to load history", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadHistory() {
        val userId = sessionManager.getUserId()

        viewModel.loadHistory(userId)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_history
    }
}
