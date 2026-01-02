package com.achraf.jeudepoints.data.model

import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("x")
    val x: Int = 0,
    @SerializedName("y")
    val y: Int = 0
)
