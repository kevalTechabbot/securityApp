package com.learning.android.wallet.model.error

import java.io.Serializable

data class InternalServerErrorModelResponse(
    val Status: String?,
    val Code: String?,
    val Message: String?,
    val Title: String?,
) : Serializable