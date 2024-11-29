package com.learning.android.wallet.network

data class ApiState<T>(
    val localStatus: Status,
    val localError: String? = null,
    var response: T? = null,
    val isResponseFromApi: Boolean = false,
)