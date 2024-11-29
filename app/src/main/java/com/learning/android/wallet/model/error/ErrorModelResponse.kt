package com.learning.android.wallet.model.error

import java.io.Serializable

data class ErrorModelResponse(
    val status: String?,
    val code: String?,
    val message: String?,
    val title: String?,
) : Serializable