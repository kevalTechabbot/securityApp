package com.learning.android.wallet.network

import android.content.Context
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

object TempApiResponses {
    // Just Demo Responses
    fun loginResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.LOGIN_RESPONSE_JSON).bufferedReader()
                .use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun signupResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.SIGNUP_RESPONSE_JSON).bufferedReader()
                .use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun addPinResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.ADD_PIN_RESPONSE_JSON).bufferedReader()
                .use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun resetPinResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.RESET_PIN_RESPONSE_JSON)
                .bufferedReader()
                .use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun verifyPinResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.VERIFY_PIN_RESPONSE_JSON)
                .bufferedReader()
                .use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun bioLoginResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.BIO_RESPONSE_JSON).bufferedReader()
                .use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun resetForgotPasswordResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.RESET_FORGOT_PASSWORD_RESPONSE_JSON)
                .bufferedReader().use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun resetPasswordResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.RESET_PASSWORD_RESPONSE_JSON)
                .bufferedReader().use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun forgotPasswordResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.FORGOT_PASSWORD_RESPONSE_JSON)
                .bufferedReader().use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun verifyPasswordResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.VERIFY_PASSWORD_RESPONSE_JSON)
                .bufferedReader().use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun verifyVerificationCodeResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.VERIFY_VERIFICATION_CODE_RESPONSE_JSON)
                .bufferedReader().use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun bankListResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.BANK_LIST_RESPONSE_JSON)
                .bufferedReader()
                .use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun cardAndTransactionListResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.CARD_TRANSACTION_LIST_RESPONSE_JSON)
                .bufferedReader().use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }

    fun transactionListResponse(
        context: Context
    ): Response<ResponseBody> {
        val responseBody =
            context.assets.open(NetworkConstants.ApiResponse.TRANSACTION_LIST_RESPONSE_JSON)
                .bufferedReader().use { it.readText() }.toResponseBody(null)
        return Response.success(responseBody)
    }
}