package com.learning.android.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.pm.PackageInfoCompat
import com.learning.android.BuildConfig
import com.learning.android.R
import com.learning.android.wallet.network.CommonProvider
import com.learning.android.wallet.model.error.ErrorModelResponse
import com.learning.android.wallet.model.error.InternalServerErrorModelResponse
import com.learning.android.wallet.model.success.SuccessResponse
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.network.NetworkConstants
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.ui.dashboard.DashboardActivity
import com.learning.android.wallet.ui.login.LoginActivity
import com.learning.android.wallet.ui.passcode.PasscodeActivity
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_NOTICE
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_SUCCESS
import com.learning.android.utils.AppConstants.UNAUTHORISED_STATUS
import com.learning.android.utils.ExtensionUtils.Common.showConfirmationDialog
import com.learning.android.utils.fromJson
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

object AppUtils {

    fun showToastWithContext(context: Context, message: String, messageBackgroundColor: Int) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            val activity = getActivity(context)
            if (activity != null) {
                val color = when (messageBackgroundColor) {
                    MESSAGE_COLOR_ERROR -> {
                        R.color.red_400
                    }

                    MESSAGE_COLOR_NOTICE -> {
                        R.color.amber_400
                    }

                    MESSAGE_COLOR_SUCCESS -> {
                        R.color.light_green_400
                    }

                    else -> {
                        R.color.red_400
                    }
                }
//                Alerter.create(activity).hideIcon().setBackgroundColorRes(color).setTitle(message)
//                    .show()
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun isNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun isValidEmail(target: String?): Boolean {
        return !TextUtils.isEmpty(target ?: "") && Patterns.EMAIL_ADDRESS.matcher(target ?: "")
            .matches()
    }

    fun Activity.hideSoftKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun providesVersionCode(context: Context): String {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName, PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        return PackageInfoCompat.getLongVersionCode(packageInfo).toString()
    }

    fun providesVersionName(context: Context): String {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName, PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        return packageInfo.versionName
    }

    fun Activity.closeKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun getActivity(context: Context): Activity? {
        if (context is ContextWrapper) {
            return if (context is Activity) {
                context
            } else {
                getActivity(context.baseContext)
            }
        }
        return null
    }

    private var progressDialog: Dialog? = null

    private fun Context.createProgressDialog(): Dialog {
        val progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        val dialog = Dialog(this).apply {
            setContentView(progressBar)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

    fun Context.showProgress() {
        progressDialog = createProgressDialog()
        progressDialog?.show()
    }

    fun dismissProgress() {
        progressDialog?.dismiss()
    }

    suspend inline fun <reified T> getDataFromDatabaseOrApiLogic(
        context: Context,
        apiResponse: () -> Deferred<Response<ResponseBody>?>,
        isFailureMessageShow: Boolean,
        isSuccessMessageShow: Boolean,
        showProgress: Boolean = true,
        dismissProgress: Boolean = showProgress,
    ): ApiState<T> {
        val getResponse: Response<ResponseBody>?
        val apiState = if (isNetwork(context)) {
            if (showProgress) context.showProgress()
            getResponse = apiResponse.invoke().await()
            try {
                if (getResponse != null) {

                    if (getResponse.code() in 200..299) {
                        // Success Body
                        var successMsg = getResponse.message().toString()
                        val responseBody = getResponse.body()
                        val convertedResponse = CommonProvider.gson()
                            .fromJson<SuccessResponse<T>?>(responseBody?.string().toString())
                        if (convertedResponse?.message != null) successMsg =
                            convertedResponse.message

                        when (getResponse.code()) {
                            NetworkConstants.ApiCode.SUCCESS_CODE -> {
                                if (convertedResponse != null) {
                                    logI("come", "1")
                                    val newResponse = convertedResponse.data as T
                                    if (isSuccessMessageShow) showToastWithContext(
                                        context, successMsg, MESSAGE_COLOR_SUCCESS
                                    )
                                    ApiState(
                                        Status.SUCCESS, response = newResponse
                                    )
                                } else {
                                    logI("come", "2")
                                    showToastWithContext(
                                        context, getResponse.message(), MESSAGE_COLOR_ERROR
                                    )
                                    ApiState(
                                        localError = getResponse.message(),
                                        localStatus = Status.ERROR
                                    )
                                }
                            }

                            NetworkConstants.ApiCode.FAILURE_CODE_202 -> {
                                logI("come", "3")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, successMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.FAIL, successMsg
                                )
                            }

                            NetworkConstants.ApiCode.NO_CONTENT -> {
                                logI("come", "4")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, successMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.FAIL, successMsg
                                )
                            }

                            else -> {
                                logI("come", "5")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, successMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.ERROR,
                                    successMsg,
                                )
                            }
                        }

                    } else if (getResponse.code() == 500) {
                        // Internal Server Error Body
                        var errorMsg = getResponse.message().toString()
                        try {
                            val errorBody = getResponse.errorBody()
                            val errorModel =
                                CommonProvider.gson().fromJson<InternalServerErrorModelResponse>(
                                    errorBody?.string().toString()
                                )
                            if (errorModel.Message != null) errorMsg = errorModel.Message
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        logI("come", "10")
                        if (isFailureMessageShow) showToastWithContext(
                            context, errorMsg, MESSAGE_COLOR_ERROR
                        )
                        ApiState(
                            Status.ERROR, errorMsg
                        )
                    } else {
                        // Error Body
                        var errorMsg = getResponse.message().toString()
                        try {
                            val errorBody = getResponse.errorBody()
                            val errorModel = CommonProvider.gson()
                                .fromJson<ErrorModelResponse>(errorBody?.string().toString())
                            if (errorModel.message != null) errorMsg = errorModel.message
                            else if (errorModel.title != null) errorMsg = errorModel.title
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        when (getResponse.code()) {
                            NetworkConstants.ApiCode.BAD_REQUEST -> {
                                logI("come", "6")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, errorMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState<T>(
                                    Status.FAIL, errorMsg, null
                                )
                            }

                            NetworkConstants.ApiCode.UN_AUTHORIZE -> {
                                logI("come", "7")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, errorMsg, MESSAGE_COLOR_ERROR
                                )
                                val prefUtils = CommonProvider.providesSharedPreference(context)
                                prefUtils.putBoolean(UNAUTHORISED_STATUS, true)
                                context.startActivity(
                                    Intent(context, PasscodeActivity::class.java).putExtra(
                                        AppConstants.MPinActionStatus.STATUS_KEY,
                                        AppConstants.MPinActionStatus.SPLASH
                                    )
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        .setAction("your.custom.action")
                                )
                                ApiState(
                                    Status.UNAUTHORISED, errorMsg
                                )
                            }

                            NetworkConstants.ApiCode.FAILURE_CODE_422 -> {
                                logI("come", "8")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, errorMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.ERROR, errorMsg
                                )
                            }

                            NetworkConstants.ApiCode.NO_API_FOUND -> {
                                logI("come", "9")
                                if (isFailureMessageShow) showToastWithContext(
                                    context,
                                    NetworkConstants.ErrorMsg.API_NOT_FOUND_MSG,
                                    MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.ERROR, NetworkConstants.ErrorMsg.API_NOT_FOUND_MSG
                                )
                            }

                            NetworkConstants.ApiCode.UN_SUPPORTED_MEDIA_TYPE -> {
                                logI("come", "11")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, errorMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.ERROR, errorMsg
                                )
                            }

                            NetworkConstants.ApiCode.INTERNAL_ERROR_CODE -> {
                                logI("come", "12")
                                dismissProgress()
                                if (isFailureMessageShow) showToastWithContext(
                                    context, errorMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.ERROR, errorMsg
                                )
                            }

                            else -> {
                                logI("come", "13")
                                if (isFailureMessageShow) showToastWithContext(
                                    context, errorMsg, MESSAGE_COLOR_ERROR
                                )
                                ApiState(
                                    Status.ERROR,
                                    errorMsg,
                                )
                            }
                        }
                    }

                } else {
                    logI("come", "14")
                    showToastWithContext(
                        context, NetworkConstants.ErrorMsg.SOMETHING_WENT_WRONG, MESSAGE_COLOR_ERROR
                    )
                    ApiState(
                        localError = NetworkConstants.ErrorMsg.SOMETHING_WENT_WRONG,
                        localStatus = Status.ERROR
                    )
                }
            } catch (e: Exception) {
                logI("come", "15")
                val message =
                    if (e.localizedMessage == null) NetworkConstants.ErrorMsg.SOMETHING_WENT_WRONG else e.localizedMessage
                e.printStackTrace()
                showToastWithContext(context, message, MESSAGE_COLOR_ERROR)
                ApiState(localStatus = Status.ERROR, localError = message)
            }
        } else {
            logI("come", "16")
            showToastWithContext(
                context, NetworkConstants.ErrorMsg.NO_NETWORK, MESSAGE_COLOR_NOTICE
            )
            ApiState(
                localError = NetworkConstants.ErrorMsg.NO_NETWORK, localStatus = Status.NO_INTERNET
            )
        }
        if (dismissProgress) dismissProgress()
        return apiState
    }

    fun logI(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.i("My Wallet App", "$tag: $msg")
    }

    fun logE(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.e("My Wallet App", "$tag: $msg")
    }

    fun logE(tag: String, msg: String, throwable: Throwable) {
        if (BuildConfig.DEBUG) Log.e("My Wallet App", "$tag: $msg", throwable)
    }

    fun logD(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.d("My Wallet App", "$tag: $msg")
    }

    fun logW(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.w("My Wallet App", "$tag: $msg")
    }

    fun Activity.doLogout(goToDashboard: Boolean = false) {
        showConfirmationDialog(title = getString(R.string.logout_),
            message = getString(R.string.are_you_sure_you_want_to_logout),
            {
                logout()
            },
            {
                if (goToDashboard) {
                    startActivity(
                        Intent(
                            this, DashboardActivity::class.java
                        ).setAction("your.custom.action")
                    )
                    finish()
                }
            })
    }

    private fun Activity.logout() {
        val sharedPref = PrefUtils(this)
        sharedPref.putBoolean(AppConstants.IS_LOGIN, false)
        sharedPref.putString(AppConstants.LOGIN_PASSCODE, "")
        sharedPref.putString(AppConstants.TOKEN, "")
        startActivity(
            Intent(
                this, LoginActivity::class.java
            ).setAction("your.custom.action")
        )
        finish()
    }

    fun generateRandomDecimal(min: Double = 0.0, max: Double = 100.0): Double {
        require(min < max) { "min must be less than max" }

        return Random.nextDouble(min, max)
    }

    fun Context.getInstalledPackageIds(): List<PackageInfo> {
        return packageManager.getInstalledPackages(PackageManager.GET_META_DATA).filter {
            it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
        }
    }

    fun openAppByPackageId(context: Context, packageId: String?) {
        val packageManager: PackageManager = context.packageManager

        if (packageId == null) {
            showToastWithContext(context, "Package is null", MESSAGE_COLOR_ERROR)
            return
        }
        // Check if the app is installed
        val intent = packageManager.getLaunchIntentForPackage(packageId)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            openAppOnPlayStore(context, packageId)
        }
    }

    private fun openAppOnPlayStore(context: Context, packageId: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageId")
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageId")
                )
            )
        }
    }

    fun String?.convertServerDateTimeToApp(): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

        return if (this != null) {
            // Parse the original date string
            val originalDate = originalFormat.parse(this)

            // Format the date in the desired output format
            val outputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            if (originalDate != null) outputFormat.format(originalDate)
            else ""
        } else ""
    }

    fun String.convertCardNumberToStar(): String {
        return "***" + this.takeLast(4)
    }

    fun String.copyToClipboard(context: Context) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Text", this)
        clipboardManager.setPrimaryClip(clipData)
        showToastWithContext(
            context,
            context.getString(R.string.transaction_id_has_been_copied),
            MESSAGE_COLOR_SUCCESS
        )
    }
}