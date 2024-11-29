package com.learning.android.wallet.model.forgotpassword.request

data class ForgotPasswordRequest(
    val email: String? = null,
    val token: String? = null,
    val newPassword: String? = null
)