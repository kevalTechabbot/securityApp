package com.learning.android.wallet.repository.signup

import android.content.Context
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.RepositoryBase
import com.learning.android.wallet.model.signup.request.SignupRequest
import com.learning.android.utils.AppUtils

class SignupRepository(val context: Context) : RepositoryBase(context) {
    // Signup API
    suspend inline fun <reified T> signupApi(
        signupRequest: SignupRequest,
        isSuccessMessageShow: Boolean = true,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.signupApi(signupRequest) }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }
}