package com.achraf.jeudepoints.data.model

import com.google.gson.annotations.SerializedName

data class LeaderboardEntry(
    @SerializedName("rank")
    val rank: Int,

    @SerializedName("playerId")
    val playerId: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("totalScore")
    val totalScore: Int,

    @SerializedName("gamesPlayed")
    val gamesPlayed: Int,

    @SerializedName("averageScore")
    val averageScore: Double
) {
    fun getDisplayName(): String = fullName ?: username
}