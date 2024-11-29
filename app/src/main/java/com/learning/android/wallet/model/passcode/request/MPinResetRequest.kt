package com.learning.android.wallet.model.passcode.request

data class MPinResetRequest(
    val newMPin: String? = null, val oldMPin: String? = null, val userId: String? = null
)