package com.learning.android.wallet.ui.login

import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.learning.android.BuildConfig
import com.learning.android.R
import com.learning.android.databinding.ActivityLoginBinding
import com.learning.android.wallet.model.login.LoginResponse
import com.learning.android.wallet.model.login.request.LoginRequest
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.login.LoginRepository
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.ui.forgotpassword.ForgotPasswordActivity
import com.learning.android.wallet.ui.login.viewmodel.LoginViewModel
import com.learning.android.wallet.ui.login.viewmodel.LoginViewModelFactory
import com.learning.android.wallet.ui.passcode.PasscodeActivity
import com.learning.android.wallet.ui.signup.SignUpActivity
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppConstants.IS_LOGIN
import com.learning.android.utils.AppConstants.LOGIN_USER_NAME
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
import com.learning.android.utils.AppConstants.TOKEN
import com.learning.android.utils.AppConstants.USER_ID
import com.learning.android.utils.ExtensionUtils.EditTextUtils.getTextString
import com.learning.android.utils.ExtensionUtils.EditTextUtils.isEmpty
import com.learning.android.utils.ExtensionUtils.EditTextUtils.onDone
import com.learning.android.utils.SpannableText

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var repository: LoginRepository

    override fun getBinding(inflater: LayoutInflater): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityLoginBinding) {
        binding.apply {
            editTextPassword.onDone {
                buttonLogin.performClick()
            }
        }
    }

    override fun initViews(binding: ActivityLoginBinding) {
        repository = LoginRepository(this)
        viewModel = ViewModelProvider(
            this, LoginViewModelFactory(repository)
        )[LoginViewModel::class.java]

        binding.apply {
            editTextUserName.setText("test456@gmail.com");
            editTextPassword.setText("12345678");
            tvLoginPortionBuildName.text = getString(
                R.string.version_code_version_name,
                BuildConfig.VERSION_CODE.toString(),
                BuildConfig.VERSION_NAME
            )

            val termsPrivacyFullText =
                getString(R.string.it_s_your_terms_amp_conditions_and_privacy_policy)
            val strTermsCondition = getString(R.string.terms_and_conditions_part)
            val strPrivacyPolicy = getString(R.string.privacy_policy_part)
            val spanColor = ContextCompat.getColor(this@LoginActivity, R.color.colorPrimary)

            val termsPrivacySpannableString =
                SpannableText.Builder(termsPrivacyFullText).color(spanColor, 11, 30)
                    .color(spanColor, 34, termsPrivacyFullText.length).underline(11, 30)
                    .underline(34, termsPrivacyFullText.length).clickListener({
                        openWebViewScreen(
                            strTermsCondition, "https://www.google.com"
                        )
                    }, 11, 30).clickListener({
                        openWebViewScreen(
                            strPrivacyPolicy, "https://www.google.com"
                        )
                    }, 34, termsPrivacyFullText.length).build()

            tvLogInPortionTandP.text = termsPrivacySpannableString.build()
            tvLogInPortionTandP.movementMethod = LinkMovementMethod.getInstance()

        }
    }

    override fun setupToolbar(binding: ActivityLoginBinding) {}

    override fun onClickListeners(binding: ActivityLoginBinding) {
        with(binding) {
            textViewForgotPassword.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity, ForgotPasswordActivity::class.java
                    ).setAction("your.custom.action")
                )
            }
            buttonSignup.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity, SignUpActivity::class.java
                    ).setAction("your.custom.action")
                )
            }
            buttonLogin.setOnClickListener {
                if (editTextUserName.isEmpty()) {
                    showMessage(getString(R.string.please_enter_email_address), MESSAGE_COLOR_ERROR)
                } else if (editTextPassword.isEmpty()) {
                    showMessage(getString(R.string.please_enter_pin_code), MESSAGE_COLOR_ERROR)
                } else if (!cbLoginCheck.isChecked) {
                    showMessage(
                        getString(R.string.please_check_the_terms_and_condition),
                        MESSAGE_COLOR_ERROR
                    )
                } else {
                    loginApi()
                }
            }
        }
    }

    private fun loginApi() {
        binding.apply {
            viewModel.loginApi<LoginResponse?>(
                LoginRequest(
                    editTextUserName.getTextString(), editTextPassword.getTextString()
                )
            )
        }
    }

    override fun liveDataObservers(binding: ActivityLoginBinding) {
        viewModel.loginUserResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    afterLogin(it.response as LoginResponse?)
                }

                else -> {}
            }
        }
    }

    private fun afterLogin(response: LoginResponse?) {
        if (response != null) {
            sharedPref.putString(USER_ID, response.userId.toString())
            sharedPref.putString(TOKEN, response.jwt.toString())
            sharedPref.putBoolean(IS_LOGIN, true)
            sharedPref.putString(LOGIN_USER_NAME, binding.editTextUserName.text.toString())
            startActivity(
                Intent(this, PasscodeActivity::class.java).putExtra(
                    AppConstants.MPinActionStatus.STATUS_KEY, AppConstants.MPinActionStatus.LOGIN
                ).setAction("your.custom.action")
            )
            finish()
        } else {
            showMessage("Login Data is null", MESSAGE_COLOR_ERROR)
        }
    }

    override fun onBackPress() {
        super.onBackPress()
        finishAffinity()
    }
}