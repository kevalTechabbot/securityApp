package com.learning.android.wallet.model.signup.request

data class SignupRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val password: String? = null,
    val mobileNo: String? = null,
    val email: String? = null
)