package com.learning.android.wallet.repository.resetpassword

import android.content.Context
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.RepositoryBase
import com.learning.android.wallet.model.resetpassword.request.ResetPasswordRequest
import com.learning.android.utils.AppUtils

class ResetPasswordRepository(val context: Context) : RepositoryBase(context) {
    // Reset Password API
    suspend inline fun <reified T> resetPasswordApi(
        resetPasswordRequest: ResetPasswordRequest,
        isSuccessMessageShow: Boolean = true,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.resetPasswordApi(resetPasswordRequest) }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }
}