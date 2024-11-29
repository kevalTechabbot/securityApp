package com.learning.android.wallet.repository.login

import android.content.Context
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.RepositoryBase
import com.learning.android.wallet.model.login.request.LoginRequest
import com.learning.android.utils.AppUtils

class LoginRepository(val context: Context) : RepositoryBase(context) {
    // Login API
    suspend inline fun <reified T> loginApi(
        loginRequest: LoginRequest,
        isSuccessMessageShow: Boolean = false,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.loginApi(loginRequest) }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }
}