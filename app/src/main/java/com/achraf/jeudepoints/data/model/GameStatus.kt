package com.achraf.jeudepoints.data.model

enum class GameStatus(val displayName: String) {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    FAILED("Failed");

    companion object {
        fun fromString(value: String): GameStatus {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: NOT_STARTED
        }
    }
}