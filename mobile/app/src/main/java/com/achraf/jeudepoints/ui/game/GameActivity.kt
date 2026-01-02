package com.achraf.jeudepoints.ui.game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.achraf.jeudepoints.R
import com.achraf.jeudepoints.data.model.GameDifficulty
import com.achraf.jeudepoints.data.model.GameSession
import com.achraf.jeudepoints.data.model.GameStatus
import com.achraf.jeudepoints.databinding.ActivityGameBinding
import com.achraf.jeudepoints.utils.Resource
import com.achraf.jeudepoints.utils.SessionManager

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedDifficulty: GameDifficulty = GameDifficulty.MEDIUM
    private var hasActiveGame = false

    private val pollingHandler = Handler(Looper.getMainLooper())
    private var isPolling = false
    private val pollingIntervalMs = 3000L

    private var lastCompletedGameId: Long? = null
    private var isInitialLoad = true
    private var previousGameStatus: GameStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        setupToolbar()
        setupListeners()
        observeViewModel()

        val difficultyFromIntent = intent.getStringExtra("DIFFICULTY")
        if (difficultyFromIntent != null) {
            selectedDifficulty = GameDifficulty.fromString(difficultyFromIntent)
            selectDifficulty(selectedDifficulty)
        } else {
            checkForActiveGame()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        binding.btnEasy.setOnClickListener {
            selectDifficulty(GameDifficulty.EASY)
        }

        binding.btnMedium.setOnClickListener {
            selectDifficulty(GameDifficulty.MEDIUM)
        }

        binding.btnHard.setOnClickListener {
            selectDifficulty(GameDifficulty.HARD)
        }

        binding.btnStartGame.setOnClickListener {
            showStartGameConfirmation()
        }
    }

    private fun observeViewModel() {
        viewModel.currentGame.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { gameData ->
                        hasActiveGame = true
                        updateUIWithGameData(gameData)

                        if (gameData.status == GameStatus.IN_PROGRESS) {
                            startPolling()
                        } else {
                            stopPolling()
                        }
                    } ?: run {
                        hasActiveGame = false
                        showDifficultySelection()
                        stopPolling()
                    }
                    isInitialLoad = false
                }
                is Resource.Error -> {
                    if (isInitialLoad || result.message?.contains("No active game") == true) {
                        hasActiveGame = false
                        showDifficultySelection()
                        stopPolling()
                        isInitialLoad = false
                    }
                }
                is Resource.Loading -> {
                    if (isInitialLoad) {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.gameStarted.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.game_started), Toast.LENGTH_SHORT).show()

                    result.data?.let { gameData ->
                        updateUIWithGameData(gameData)
                        startPolling()

                        if (gameData.status == GameStatus.IN_PROGRESS) {
                            showGameStartedDialog()
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message ?: "Failed to start game", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvInfoText.text = getString(R.string.waiting_for_game)
                }
            }
        }
    }

    private fun checkForActiveGame() {
        val userId = sessionManager.getUserId()

        viewModel.loadActiveGame(userId)
    }

    private fun selectDifficulty(difficulty: GameDifficulty) {
        selectedDifficulty = difficulty

        binding.tvDifficulty.text = difficulty.displayName

        resetDifficultyButtons()

        when (difficulty) {
            GameDifficulty.EASY -> binding.btnEasy.strokeWidth = 4
            GameDifficulty.MEDIUM -> binding.btnMedium.strokeWidth = 4
            GameDifficulty.HARD -> binding.btnHard.strokeWidth = 4
        }

        binding.btnStartGame.visibility = View.VISIBLE
        binding.tvInfoText.text = "Difficulty: ${difficulty.displayName} selected. Tap Start Game."
    }

    private fun resetDifficultyButtons() {
        binding.btnEasy.strokeWidth = 2
        binding.btnMedium.strokeWidth = 2
        binding.btnHard.strokeWidth = 2
    }

    private fun showDifficultySelection() {
        binding.cardDifficultySelection.visibility = View.VISIBLE
        binding.btnStartGame.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.tvInfoText.text = getString(R.string.select_difficulty)
    }

    private fun hideDifficultySelection() {
        binding.cardDifficultySelection.visibility = View.GONE
        binding.btnStartGame.visibility = View.GONE
    }

    private fun showStartGameConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Start Game")
            .setMessage("Start a ${selectedDifficulty.displayName} difficulty game?\n\nThis will signal the ESP32 to begin the game.")
            .setPositiveButton("Start") { _, _ ->
                startGame()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startGame() {
        val userId = sessionManager.getUserId()

        lastCompletedGameId = null
        previousGameStatus = null
        isInitialLoad = true

        viewModel.startGame(userId, selectedDifficulty)
        hideDifficultySelection()
    }

    private fun updateUIWithGameData(game: GameSession) {
        binding.progressBar.visibility = View.GONE

        binding.tvStatus.text = game.status.displayName
        val statusColor = when (game.status) {
            GameStatus.IN_PROGRESS -> R.color.game_in_progress
            GameStatus.COMPLETED -> R.color.game_completed
            GameStatus.FAILED -> R.color.game_failed
            else -> R.color.grey
        }
        binding.tvStatus.setTextColor(getColor(statusColor))

        binding.tvDifficulty.text = game.difficulty.displayName
        binding.tvPointsCollected.text = (game.score ?: 0.0).toInt().toString()

        val durationSeconds = game.movementTime?.toLong() ?: 0L
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        binding.tvDuration.text = String.format("%d:%02d", minutes, seconds)

        val statusChanged = previousGameStatus != game.status
        previousGameStatus = game.status

        when (game.status) {
            GameStatus.IN_PROGRESS -> {
                binding.tvInfoText.text = "Game in progress on ESP32..."
                hideDifficultySelection()
            }
            GameStatus.COMPLETED -> {
                binding.tvInfoText.text = "Game completed!"
                showDifficultySelection()

                if (statusChanged && game.id != lastCompletedGameId) {
                    showGameCompletedNotification(game)
                    lastCompletedGameId = game.id
                }
            }
            GameStatus.FAILED -> {
                binding.tvInfoText.text = "Game failed. Try again!"
                showDifficultySelection()
            }
            else -> {
                binding.tvInfoText.text = getString(R.string.select_difficulty)
            }
        }
    }

    private fun showGameStartedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Game Started!")
            .setMessage("The game has been started on your ESP32 device.\n\nYou can now begin playing. This app will update automatically as you progress.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showGameCompletedNotification(game: GameSession) {
        val score = game.score?.toInt() ?: 0
        Toast.makeText(
            this,
            "Game Completed! Score: $score XP",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun startPolling() {
        if (isPolling) return
        isPolling = true
        pollingHandler.post(pollingRunnable)
    }

    private fun stopPolling() {
        isPolling = false
        pollingHandler.removeCallbacks(pollingRunnable)
    }

    private val pollingRunnable = object : Runnable {
        override fun run() {
            if (isPolling && hasActiveGame) {
                val userId = sessionManager.getUserId()
                viewModel.loadActiveGame(userId)
            }

            if (isPolling) {
                pollingHandler.postDelayed(this, pollingIntervalMs)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasActiveGame) {
            isInitialLoad = true
            checkForActiveGame()
        }
    }

    override fun onPause() {
        super.onPause()
        stopPolling()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPolling()
    }
}
