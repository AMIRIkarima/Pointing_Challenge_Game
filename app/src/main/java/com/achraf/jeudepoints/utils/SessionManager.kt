package com.achraf.jeudepoints.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString(Constants.KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }

    fun saveUserId(userId: Long) {
        prefs.edit().putLong(Constants.KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Long {
        return prefs.getLong(Constants.KEY_USER_ID, -1L)
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(Constants.KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(Constants.KEY_USERNAME, null)
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null && getUserId() != -1L
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun getAuthHeader(): String {
        return "Bearer ${getAuthToken()}"
    }
}