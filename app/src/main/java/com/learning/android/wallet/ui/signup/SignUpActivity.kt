package com.learning.android.wallet.ui.signup

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.learning.android.BuildConfig
import com.learning.android.R
import com.learning.android.databinding.ActivitySignupBinding
import com.learning.android.wallet.model.login.LoginResponse
import com.learning.android.wallet.model.signup.request.SignupRequest
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.signup.SignupRepository
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.ui.signup.viewmodel.SignupViewModel
import com.learning.android.wallet.ui.signup.viewmodel.SignupViewModelFactory
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
import com.learning.android.utils.AppUtils
import com.learning.android.utils.ExtensionUtils.EditTextUtils.getTextString
import com.learning.android.utils.ExtensionUtils.EditTextUtils.onDone
import com.learning.android.utils.SpannableText

class SignUpActivity : BaseActivity<ActivitySignupBinding>() {

    private lateinit var viewModel: SignupViewModel
    private lateinit var repository: SignupRepository

    override fun getBinding(inflater: LayoutInflater): ActivitySignupBinding {
        return ActivitySignupBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivitySignupBinding) {
        binding.apply {
            editTextPhoneNumber.onDone {
                buttonSignup.performClick()
            }
        }
    }

    override fun initViews(binding: ActivitySignupBinding) {
        repository = SignupRepository(this)
        viewModel = ViewModelProvider(
            this, SignupViewModelFactory(repository)
        )[SignupViewModel::class.java]

        binding.apply {
            tvLoginPortionBuildName.text = getString(
                R.string.version_code_version_name,
                BuildConfig.VERSION_CODE.toString(),
                BuildConfig.VERSION_NAME
            )

            val termsPrivacyFullText =
                getString(R.string.it_s_your_terms_amp_conditions_and_privacy_policy)
            val strTermsCondition = getString(R.string.terms_and_conditions_part)
            val strPrivacyPolicy = getString(R.string.privacy_policy_part)
            val spanColor = ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary)

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

            tvSignupPortionTandP.text = termsPrivacySpannableString.build()
            tvSignupPortionTandP.movementMethod = LinkMovementMethod.getInstance()

        }
    }

    override fun setupToolbar(binding: ActivitySignupBinding) {}

    override fun onClickListeners(binding: ActivitySignupBinding) {
        with(binding) {
            buttonSignup.setOnClickListener {
                if (editTextFirstName.getTextString() == "") {
                    showMessage(getString(R.string.please_enter_first_name), MESSAGE_COLOR_ERROR)
                    return@setOnClickListener
                }
                if (editTextLastName.getTextString() == "") {
                    showMessage(getString(R.string.please_enter_last_name), MESSAGE_COLOR_ERROR)
                    return@setOnClickListener
                }
                if (!AppUtils.isValidEmail(editTextEmail.getTextString())) {
                    showMessage(
                        getString(R.string.please_enter_valid_email_address), MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                if (editTextPassword.getTextString().isEmpty()) {
                    showMessage(
                        getString(R.string.please_enter_password),
                        MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                if (editTextPassword.getTextString().length < 4 || editTextPassword.getTextString().length > 8) {
                    showMessage(
                        getString(R.string.password_length_should_not_be_less_than_4_and_greater_than_8),
                        MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                if (editTextConfirmPassword.getTextString() != editTextPassword.getTextString()) {
                    showMessage(
                        getString(R.string.confirm_password_is_not_same_as_password),
                        MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                if (editTextPhoneNumber.getTextString() == "") {
                    showMessage(getString(R.string.please_enter_phone_number), MESSAGE_COLOR_ERROR)
                    return@setOnClickListener
                }
                if (!cbSignupCheck.isChecked) {
                    showMessage(
                        getString(R.string.please_check_the_terms_and_condition),
                        MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                signupApi()
            }
            textViewLogin.setOnClickListener {
                finish()
            }
        }
    }

    private fun signupApi() {
        binding.apply {
            viewModel.signupApi<LoginResponse?>(
                SignupRequest(
                    editTextFirstName.getTextString(),
                    editTextLastName.getTextString(),
                    editTextPassword.getTextString(),
                    editTextPhoneNumber.getTextString(),
                    editTextEmail.getTextString(),
                )
            )
        }
    }

    override fun liveDataObservers(binding: ActivitySignupBinding) {
        viewModel.signUpUserResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    finish()
                }

                else -> {}
            }
        }
    }
}