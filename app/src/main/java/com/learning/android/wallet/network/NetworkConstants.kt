package com.learning.android.wallet.network

object NetworkConstants {
    object ApiUrls {
        const val BASE_URL = "https://dummyjson.com/" // Replace with your own Base url
    }

    object ApiResponse {
        const val LOGIN_RESPONSE_JSON = "Login.json"
        const val SIGNUP_RESPONSE_JSON = "Signup.json"
        const val ADD_PIN_RESPONSE_JSON = "AddMPin.json"
        const val RESET_PIN_RESPONSE_JSON = "ResetMPin.json"
        const val VERIFY_PIN_RESPONSE_JSON = "VerifyMPin.json"
        const val BIO_RESPONSE_JSON = "BiomatrixVarification.json"
        const val RESET_FORGOT_PASSWORD_RESPONSE_JSON = "ResetForgotPassword.json"
        const val RESET_PASSWORD_RESPONSE_JSON = "ResetPassword.json"
        const val FORGOT_PASSWORD_RESPONSE_JSON = "ForgotPassword.json"
        const val VERIFY_PASSWORD_RESPONSE_JSON = "VerifyPassword.json"
        const val VERIFY_VERIFICATION_CODE_RESPONSE_JSON = "VerifyVerificationCode.json"
        const val BANK_LIST_RESPONSE_JSON = "GetBankNames.json"
        const val CARD_TRANSACTION_LIST_RESPONSE_JSON = "GetCardAndTransaction.json"
        const val TRANSACTION_LIST_RESPONSE_JSON = "GetLastNTransaction.json"
    }

    object ApiEndPoint {
        const val API_ENDPOINT_USER = "api/User"
        const val API_ENDPOINT_CARD = "api/Card"
        const val API_ENDPOINT_BANK = "api/Bank"
        const val API_ENDPOINT_TEMP = "products/1"
    }

    object ApiCode {
        const val SUCCESS_CODE = 200
        const val FAILURE_CODE_202 = 202
        const val NO_INTERNET = 203
        const val NO_CONTENT = 204
        const val INTERNAL_ERROR_CODE = 1000
        const val INTERNAL_SERVER_ERROR_CODE = 500
        const val UN_CONDITIONAL_EXCEPTION = 501
        const val SESSION_TIMEOUT_EXCEPTION = 502
        const val SESSION_EXCEPTION = 503
        const val FAILURE_CODE_422 = 422
        const val BAD_REQUEST = 400
        const val UN_AUTHORIZE = 401
        const val NO_API_FOUND = 404
        const val UN_SUPPORTED_MEDIA_TYPE = 415
    }

    object ErrorMsg {
        const val UN_SUPPORTED_MEDIA_TYPE_MSG = "Request Method is wrong"
        const val SOMETHING_WENT_WRONG = "Something went wrong"
        const val API_NOT_FOUND_MSG = "API not found"
        const val LINK_IS_NOT_WORKING = "Url is not responding"
        const val NO_NETWORK = "Please check your Internet Connection"
        const val REQUEST_PARAMETER_MISSING = "Some data is missing"
    }
}