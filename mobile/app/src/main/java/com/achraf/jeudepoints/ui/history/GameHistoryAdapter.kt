package com.achraf.jeudepoints.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.achraf.jeudepoints.R
import com.achraf.jeudepoints.data.model.GameSession
import com.achraf.jeudepoints.data.model.GameStatus
import com.achraf.jeudepoints.databinding.ItemGameHistoryBinding

class GameHistoryAdapter(
    private var games: List<GameSession>
) : RecyclerView.Adapter<GameHistoryAdapter.GameViewHolder>() {

    inner class GameViewHolder(private val binding: ItemGameHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: GameSession) {
            binding.tvGameId.text = "Game #${game.id}"
            binding.tvDate.text = game.creationDate?.substring(0, 10) ?: ""
            binding.tvStatus.text = game.status.displayName
            binding.tvScore.text = (game.score ?: 0.0).toInt().toString()
            binding.tvDifficulty.text = game.difficulty.displayName

            val duration = game.movementTime?.toLong() ?: 0L
            val minutes = duration / 60
            val seconds = duration % 60
            binding.tvDuration.text = String.format("%d:%02d", minutes, seconds)

            val statusColor = when (game.status) {
                GameStatus.COMPLETED -> R.color.game_completed
                GameStatus.FAILED -> R.color.game_failed
                GameStatus.IN_PROGRESS -> R.color.game_in_progress
                else -> R.color.grey
            }
            binding.tvStatus.setTextColor(binding.root.context.getColor(statusColor))

            val difficultyColor = when (game.difficulty) {
                com.achraf.jeudepoints.data.model.GameDifficulty.EASY -> R.color.easy_color
                com.achraf.jeudepoints.data.model.GameDifficulty.MEDIUM -> R.color.medium_color
                com.achraf.jeudepoints.data.model.GameDifficulty.HARD -> R.color.hard_color
            }
            binding.tvDifficulty.setTextColor(binding.root.context.getColor(difficultyColor))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount() = games.size

    fun updateGames(newGames: List<GameSession>) {
        games = newGames
        notifyDataSetChanged()
    }
}
