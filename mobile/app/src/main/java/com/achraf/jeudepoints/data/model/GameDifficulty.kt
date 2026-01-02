package com.achraf.jeudepoints.data.model

enum class GameDifficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    companion object {
        fun fromString(value: String): GameDifficulty {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}