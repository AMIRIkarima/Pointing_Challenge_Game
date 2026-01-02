package com.achraf.jeudepoints.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("player")
    val player: Player,

    @SerializedName("message")
    val message: String? = null
)