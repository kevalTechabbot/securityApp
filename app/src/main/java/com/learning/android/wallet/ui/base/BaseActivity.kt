package com.learning.android.wallet.ui.base

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.learning.android.R
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_NOTICE
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_SUCCESS
import com.learning.android.utils.AppUtils.logI
import com.learning.android.utils.PrefUtils
import com.learning.android.wallet.model.banklist.BankListResponse
import com.learning.android.wallet.network.CommonProvider.providesSharedPreference
import com.learning.android.wallet.ui.webview.WebViewActivity
import java.io.Serializable
import java.util.concurrent.Executor

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var sharedPref: PrefUtils
    protected lateinit var mimeType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getBinding(layoutInflater)
        setContentView(binding.root)
        sharedPref = providesSharedPreference(this)
        mimeType = "application/vnd.${packageName}.${AppConstants.NFC_TAG_NAME}"
        setupToolbar(binding)
        initViews(binding)
        onClickListeners(binding)
        liveDataObservers(binding)
        onDoneKeyboardClicked(binding)
        backPress()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    protected lateinit var binding: VB
    abstract fun getBinding(inflater: LayoutInflater): VB
    abstract fun initViews(binding: VB)
    abstract fun onClickListeners(binding: VB)
    abstract fun liveDataObservers(binding: VB)
    abstract fun setupToolbar(binding: VB)
    abstract fun onDoneKeyboardClicked(binding: VB)


    fun showMessage(message: String?, messageBackgroundColor: Int) {
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        Alerter.create(this).hideIcon().setBackgroundColorRes(color).setTitle(message ?: "").show()
    }

    private fun backPress() {
        onBackPressedDispatcher.addCallback(this) {
            onBackPress()
        }
    }

    open fun onBackPress() {
        finish()
    }

    fun <T : Serializable?> Intent.getSerializableExtr(keyName: String?, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getSerializableExtra(
            keyName, clazz
        )
        else getSerializableExtra(keyName) as T?
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.PERMISSION_INTENT_REQ_CODE -> for (i in grantResults.indices) if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                logI("", "granted")
            } else {
                logI("", "not granted")
                break
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // Biometric
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor
    private lateinit var callBack: BiometricPrompt.AuthenticationCallback
    private var keyguardManager: KeyguardManager? = null

    protected fun initBiometric(
        showSuccessMsg: Boolean = true, showErrorMsg: Boolean = true, callback: (Boolean?) -> Unit
    ) {
        executor = ContextCompat.getMainExecutor(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            callBack = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (showSuccessMsg) showMessage(
                        getString(R.string.message_success),
                        Toast.LENGTH_SHORT
                    )
                    callback.invoke(true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (showErrorMsg) showMessage(errString.toString(), MESSAGE_COLOR_ERROR)
                    callback.invoke(false)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    logI("Biometric", "Biometric authentication is available and enabled.")
                    biometricPrompt = BiometricPrompt(this@BaseActivity, executor, callBack)
                }

                else -> {
                    logI("Biometric", "Else.")
                    callback.invoke(null)
                }
            }
        } else {
            logI("Biometric", "Else.")
            callback.invoke(null)
        }
    }

    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                getString(R.string.message_user_app_authentication)
            }

            BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                getString(R.string.error_hw_unavailable)
            }

            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {
                getString(R.string.error_unable_to_process)
            }

            BiometricPrompt.ERROR_TIMEOUT -> {
                getString(R.string.error_time_out)
            }

            BiometricPrompt.ERROR_NO_SPACE -> {
                getString(R.string.error_no_space)
            }

            BiometricPrompt.ERROR_CANCELED -> {
                getString(R.string.error_canceled)
            }

            BiometricPrompt.ERROR_LOCKOUT -> {
                getString(R.string.error_lockout)
            }

            BiometricPrompt.ERROR_VENDOR -> {
                getString(R.string.error_vendor)
            }

            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                getString(R.string.error_lockout_permanent)
            }

            BiometricPrompt.ERROR_USER_CANCELED -> {
                getString(R.string.error_user_canceled)
            }

            BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                checkAPILevelAndProceed()
                getString(R.string.error_no_biometrics)
            }

            BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                getString(R.string.error_hw_not_present)
            }

            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                startActivityForResul(biometricsEnrollIntent(), RC_BIOMETRICS_ENROLL)
                getString(R.string.error_no_device_credentials)
            }

            BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED -> {
                getString(R.string.error_security_update_required)
            }

            else -> {
                getString(R.string.error_unknown)
            }
        }
    }

    private fun checkAPILevelAndProceed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            startActivityForResul(setUpDeviceLockInAPIBelow23Intent(), RC_DEVICE_CREDENTIAL_ENROLL)
        } else {
            startActivityForResul(biometricsEnrollIntent(), RC_BIOMETRICS_ENROLL)
        }
    }

    private fun setUpDeviceLockInAPIBelow23Intent(): Intent {
        return Intent(Settings.ACTION_SECURITY_SETTINGS)
    }

    private fun biometricsEnrollIntent(): Intent {
        return Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        }
    }

    protected fun authenticateWithBiometrics() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(getString(R.string.title_biometric_dialog))
            setDescription(getString(R.string.text_description_biometrics_dialog))
            setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            setNegativeButtonText(getString(R.string.cancel))
        }.build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager?.let { manager ->
                if (manager.isKeyguardSecure) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    startActivityForResul(
                        setUpDeviceLockInAPIBelow23Intent(), RC_DEVICE_CREDENTIAL_ENROLL
                    )
                }
            }
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    protected fun openWebViewScreen(title: String, url: String) {
        val mIntent =
            Intent(this@BaseActivity, WebViewActivity::class.java).setAction("your.custom.action")
        mIntent.putExtra(AppConstants.BundleConstants.INTENT_WEB_TITLE, title)
        mIntent.putExtra(AppConstants.BundleConstants.INTENT_WEB_URL, url)
        startActivity(mIntent)
    }

    private var requestCodeNew = 0
    private val startActivityRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent = result.data ?: Intent()
                onActivityResul(requestCodeNew, result.resultCode, data)
            }
        }

    protected fun startActivityForResul(intent: Intent, requestCode: Int) {
        requestCodeNew = requestCode
        startActivityRegister.launch(intent)
    }

    open fun onActivityResul(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    protected fun Context.isPackageInstalled(
        allBankList: ArrayList<BankListResponse>
    ): ArrayList<BankListResponse> {
        val bankListFiltered: ArrayList<BankListResponse> = arrayListOf()

        for (indexedBankValue in allBankList) {
            try {
                logI(
                    "indexedBankValue",
                    indexedBankValue.bankName + "->" + indexedBankValue.packageName
                )
                packageManager.getPackageInfo(indexedBankValue.packageName ?: "", 0)
                bankListFiltered.add(indexedBankValue)
            } catch (e: PackageManager.NameNotFoundException) {

            }
        }
        return bankListFiltered
    }

    companion object {
        const val RC_BIOMETRICS_ENROLL = 10
        const val RC_DEVICE_CREDENTIAL_ENROLL = 18
    }
}