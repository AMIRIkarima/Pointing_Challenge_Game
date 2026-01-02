package com.achraf.jeudepoints.data.model

import com.google.gson.annotations.SerializedName

data class StartGameRequest(
    @SerializedName("playerId")
    val playerId: Long,

    @SerializedName("difficulty")
    val difficulty: String // "EASY", "MEDIUM", "HARD"
)