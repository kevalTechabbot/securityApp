package com.learning.android.wallet.repository

import android.content.Context
import com.learning.android.wallet.network.CommonProvider
import com.learning.android.wallet.network.ApiClient

open class RepositoryBase(context: Context) {
    val prefUtils = CommonProvider.providesSharedPreference(context)
    val apiClient = ApiClient(context)

}