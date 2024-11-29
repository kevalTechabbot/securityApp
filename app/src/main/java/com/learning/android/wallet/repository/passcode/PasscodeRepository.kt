package com.learning.android.wallet.repository.passcode

import android.content.Context
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.RepositoryBase
import com.learning.android.wallet.model.passcode.request.MPinAddRequest
import com.learning.android.wallet.model.passcode.request.MPinResetRequest
import com.learning.android.wallet.model.passcode.request.MPinVerifyRequest
import com.learning.android.utils.AppUtils

class PasscodeRepository(val context: Context) : RepositoryBase(context) {

    // Add mPin API
    suspend inline fun <reified T> createMPinApi(
        mPinAddRequest: MPinAddRequest,
        isSuccessMessageShow: Boolean = true,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.createMPinApi(mPinAddRequest) }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }

    // Reset mPin API
    suspend inline fun <reified T> resetMPinApi(
        mPinResetRequest: MPinResetRequest,
        isSuccessMessageShow: Boolean = true,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.resetMPinApi(mPinResetRequest) }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }

    // Verify mPin API
    suspend inline fun <reified T> verifyMPinApi(
        mPinVerifyRequest: MPinVerifyRequest,
        isSuccessMessageShow: Boolean = true,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.verifyMPinApi(mPinVerifyRequest) }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }

    // Biometrics Verification API
    suspend inline fun <reified T> biometricsVerificationApi(
        isSuccessMessageShow: Boolean = true,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.biometricsVerificationApi() }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }
}