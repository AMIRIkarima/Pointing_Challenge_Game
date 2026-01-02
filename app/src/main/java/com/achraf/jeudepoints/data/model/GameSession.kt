package com.achraf.jeudepoints.data.model

import com.google.gson.annotations.SerializedName

data class GameSession(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("playerId")
    val playerId: Long? = null,

    @SerializedName("difficulty")
    val difficulty: GameDifficulty,

    @SerializedName("score")
    val score: Double? = null,

    @SerializedName("creationDate")
    val creationDate: String? = null,

    @SerializedName("startPoint")
    val startPoint: Point? = null,

    @SerializedName("targetPoint")
    val targetPoint: Point? = null,

    @SerializedName("movementTime")
    val movementTime: Double? = null,

    @SerializedName("indexDifficulty")
    val indexDifficulty: Double? = null
) {
    val status: GameStatus
        get() = if (score == null) GameStatus.IN_PROGRESS else GameStatus.COMPLETED

    fun isActive(): Boolean = status == GameStatus.IN_PROGRESS
    fun isFinished(): Boolean = status == GameStatus.COMPLETED || status == GameStatus.FAILED
}
