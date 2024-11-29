package com.learning.android.utils;

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.learning.android.R
import com.learning.android.wallet.network.CommonProvider

class PrefUtils(context: Context) {
    private lateinit var prefs: SharedPreferences

    init {
        getPrefs(context)
    }

    fun getPrefs(context: Context): SharedPreferences {
        prefs = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
        return context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
    }

    /**
     * Store integer value
     * */
    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    /**
     * Retrieve integer value
     * */
    fun getInt(key: String): Int {
        return prefs.getInt(key, 0)
    }


    /**
     * Store string value
     * */
    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    /**
     * Retrieve string value
     * */
    fun getString(key: String): String {
        return prefs.getString(key, "") ?: ""
    }

    /**
     * Store boolean value
     * */
    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    /**
     * Retrieve boolean value
     * */
    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    /**
     * Clear current session
     * */
    fun logout() {
        prefs.edit().clear().apply()
    }

    fun putDataClass(key: String, value: Any?) {
        val gson = CommonProvider.gson()
        val json = gson.toJson(value ?: JsonObject())
        putString(key, json)
    }

    fun getDataClass(key: String, className: Class<*>): Any {
        val gson = CommonProvider.gson()
        val json: String = getString(key)
        return gson.fromJson(json, className)
    }
}