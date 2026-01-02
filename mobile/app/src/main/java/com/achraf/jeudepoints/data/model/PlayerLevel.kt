package com.achraf.jeudepoints.data.model

import com.google.gson.annotations.SerializedName

data class PlayerLevel(
    @SerializedName("totalScore")
    val totalScore: Int = 0,
    @SerializedName("level")
    val level: String = "Beginner"
)
