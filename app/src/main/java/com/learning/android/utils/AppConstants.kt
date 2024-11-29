package com.learning.android.utils

object AppConstants {

    // Flag for Screen
    const val IS_LOGIN = "isLoginNew"
    const val IS_WELCOME = "isWelcome"


    const val USER_ID = "UserId"
    const val TOKEN = "token"
    const val LOGIN_USER_NAME = "loginUserName"
    const val LOGIN_PASSCODE = "loginPasscode"
    const val UNAUTHORISED_STATUS = "unAuthorisedStatus"
    const val NFC_TAG_NAME = "WalletNfcTag"

    const val PERMISSION_INTENT_REQ_CODE = 4282

    object BundleConstants {
        const val INTENT_WEB_TITLE = "INTENT_WEB_TITLE"
        const val INTENT_WEB_URL = "INTENT_WEB_URL"
    }

    object MessageBackgroundColor {
        const val MESSAGE_COLOR_ERROR = 1
        const val MESSAGE_COLOR_NOTICE = 2
        const val MESSAGE_COLOR_SUCCESS = 3
    }

    object MpinKeyStatus {
        const val TYPE = 1
        const val DELETE = 2
        const val NEXT = 3
    }

    object MPinActionStatus {
        const val STATUS_KEY = "action"
        const val LOGIN = 1
        const val SPLASH = 2
        const val RESET = 3
    }

}