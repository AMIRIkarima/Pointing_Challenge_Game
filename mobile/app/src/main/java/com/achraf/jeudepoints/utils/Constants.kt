package com.achraf.jeudepoints.utils

object Constants {
    // Base URL - Change this to your backend URL when ready
    const val BASE_URL = "http://10.0.2.2:8080/" // For Android Emulator accessing localhost
    // For real device, use: "http://YOUR_COMPUTER_IP:8080/"

    // Shared Preferences
    const val PREFS_NAME = "jeu_de_points_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USERNAME = "username"

    // WebSocket URL (for real-time game updates)
    const val WS_URL = "ws://10.0.2.2:8080/ws/game"

    // Request timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}