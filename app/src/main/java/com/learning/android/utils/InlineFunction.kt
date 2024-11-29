package com.learning.android.utils

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

inline fun <reified T : Parcelable> Bundle.parcelableArray(key: String): Array<out T>? = when {
    SDK_INT >= 33 -> getParcelableArray(key, T::class.java)
    else -> @Suppress("DEPRECATION") (getParcelableArray(key) as Array<out T>?)
}

inline fun <reified T : Parcelable> Intent.parcelableArray(key: String): Array<out T>? = when {
    SDK_INT >= 33 -> getParcelableArrayExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") (getParcelableArrayExtra(key) as Array<out T>?)
}