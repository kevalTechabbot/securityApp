package com.learning.android.wallet.model.success

data class SuccessResponse<T>(
    val code: Int? = null,
    val data: T? = null,
    val message: String? = null,
    val status: String? = null
)