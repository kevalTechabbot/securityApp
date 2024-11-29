package com.learning.android.wallet.network

import android.content.Context
import com.learning.android.wallet.model.confirmpassword.request.ConfirmPasswordRequest
import com.learning.android.wallet.model.forgotpassword.request.ForgotPasswordRequest
import com.learning.android.wallet.model.login.request.LoginRequest
import com.learning.android.wallet.model.passcode.request.MPinAddRequest
import com.learning.android.wallet.model.passcode.request.MPinResetRequest
import com.learning.android.wallet.model.passcode.request.MPinVerifyRequest
import com.learning.android.wallet.model.resetpassword.request.ResetPasswordRequest
import com.learning.android.wallet.model.signup.request.SignupRequest
import com.learning.android.wallet.model.transactionlist.request.TransactionListRequest
import com.learning.android.wallet.network.CommonProvider.providesSharedPreference
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.ResponseBody
import retrofit2.Response

@OptIn(DelicateCoroutinesApi::class)
class ApiClient(context: Context) {
    private val sharedPref = providesSharedPreference(context)
    private val service = CommonProvider.providesApiService(context, sharedPref)
    private val tempApiResponses = TempApiResponses

    // User API EndPoint
    val tempApi = fun(_: LoginRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
        service.tempApi()
    }


    // Temp Api Response
    val loginApi = fun(_: LoginRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
        tempApiResponses.loginResponse(context)
    }

    val signupApi = fun(_: SignupRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
        tempApiResponses.signupResponse(context)
    }

    val createMPinApi =
        fun(_: MPinAddRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.addPinResponse(context)
        }

    val resetMPinApi =
        fun(_: MPinResetRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.resetPinResponse(context)
        }

    val verifyMPinApi =
        fun(_: MPinVerifyRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.verifyPinResponse(context)
        }

    val sendPasswordResetEmailApi =
        fun(_: ForgotPasswordRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.forgotPasswordResponse(context)
        }

    val tokenValidationApi =
        fun(_: ForgotPasswordRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.verifyVerificationCodeResponse(context)
        }

    val resetForgotPasswordApi =
        fun(_: ForgotPasswordRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.resetForgotPasswordResponse(context)
        }

    val verifyPasswordApi =
        fun(_: ConfirmPasswordRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.verifyPasswordResponse(context)
        }

    val resetPasswordApi =
        fun(_: ResetPasswordRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.resetPasswordResponse(context)
        }

    val biometricsVerificationApi = fun(): Deferred<Response<ResponseBody>> = GlobalScope.async {
        tempApiResponses.bioLoginResponse(context)
    }


    // Card API EndPoint
    val getCardAndTransactionApi = fun(): Deferred<Response<ResponseBody>> = GlobalScope.async {
        tempApiResponses.cardAndTransactionListResponse(context)
    }

    val getLastNTransactionApi =
        fun(_: TransactionListRequest): Deferred<Response<ResponseBody>> = GlobalScope.async {
            tempApiResponses.transactionListResponse(context)
        }


    // Bank API EndPoint
    val getBankNamesApi = fun(): Deferred<Response<ResponseBody>> = GlobalScope.async {
        tempApiResponses.bankListResponse(context)
    }
}