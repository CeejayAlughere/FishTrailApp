package com.fishtrail.app.settings

import android.content.Context

object AppPreferences {

    private const val PREFS_NAME = "fishtrail_preferences"

    private const val KEY_DARK_MODE = "dark_mode"

    private fun getPreferences(context: Context) =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    fun isDarkModeEnabled(context: Context): Boolean {

        return getPreferences(context)
            .getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkModeEnabled(
        context: Context,
        enabled: Boolean
    ) {

        getPreferences(context)
            .edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }
}