package com.learning.android.wallet.model.resetpassword.request

data class ResetPasswordRequest(
    val oldPassword: String? = null,
    val newPassword: String? = null,
)