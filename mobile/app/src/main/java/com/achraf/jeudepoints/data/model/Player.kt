package com.achraf.jeudepoints.data.model

import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("username")
    val username: String,

    // Player progression returned by backend
    @SerializedName("level")
    val level: PlayerLevel? = null,

    // Regression params (may be null if not computed yet)
    @SerializedName("fittsA")
    val fittsA: Double? = null,

    @SerializedName("fittsB")
    val fittsB: Double? = null,

    // History is embedded on the player payload
    @SerializedName("games")
    val games: List<GameSession> = emptyList()
) {
    val totalScore: Int
        get() = level?.totalScore ?: 0

    val rankName: String
        get() = level?.level ?: "Beginner"
}
