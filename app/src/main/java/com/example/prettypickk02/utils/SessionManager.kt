package com.example.prettypickk02.utils

import android.content.Context

object SessionManager {

    private const val PREF_NAME = "prettypick_session"
    private const val KEY_IS_ADMIN = "is_admin_logged_in"

    fun setAdminLoggedIn(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_ADMIN, isLoggedIn).apply()
    }

    fun isAdminLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_ADMIN, false)
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}