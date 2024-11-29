package com.learning.android.wallet.ui.forgotpassword

import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.learning.android.R
import com.learning.android.databinding.ActivityForgotPasswordBinding
import com.learning.android.wallet.model.forgotpassword.response.ForgotPasswordEmailResponse
import com.learning.android.wallet.model.login.LoginResponse
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.forgotpassword.ForgotPasswordRepository
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.model.forgotpassword.request.ForgotPasswordRequest
import com.learning.android.wallet.ui.forgotpassword.viewmodel.ForgotPasswordViewModel
import com.learning.android.wallet.ui.forgotpassword.viewmodel.ForgotPasswordViewModelFactory
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
import com.learning.android.utils.AppUtils.logI
import com.learning.android.utils.ExtensionUtils.Common.showConfirmationDialog
import com.learning.android.utils.ExtensionUtils.EditTextUtils.clear
import com.learning.android.utils.ExtensionUtils.EditTextUtils.getTextString
import com.learning.android.utils.ExtensionUtils.EditTextUtils.isEmpty
import com.learning.android.utils.ExtensionUtils.EditTextUtils.onDone

class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {

    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var repository: ForgotPasswordRepository

    override fun getBinding(inflater: LayoutInflater): ActivityForgotPasswordBinding {
        return ActivityForgotPasswordBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityForgotPasswordBinding) {
        binding.apply {
            tieForgotPasswordEmailBlocEmail.onDone {
                btForgotPasswordEmailBlocNext.performClick()
            }
            tieForgotPasswordNewPasswordBlocConfirmPassword.onDone {
                btForgotPasswordNewPasswordBlocSubmit.performClick()
            }
        }
    }

    override fun initViews(binding: ActivityForgotPasswordBinding) {
        repository = ForgotPasswordRepository(this)
        viewModel = ViewModelProvider(
            this, ForgotPasswordViewModelFactory(repository)
        )[ForgotPasswordViewModel::class.java]
        binding.ovForgotPasswordVerificationCode.setOtpCompletionListener {
            logI("Actual Value", it)
//            binding.btForgotPasswordVerificationCodeVerify.performClick()
        }
        blocManagement(ForgotPasswordBlocManagementStatus.EMAIL)
    }

    private fun blocManagement(forgotPasswordBlocManagementStatus: ForgotPasswordBlocManagementStatus) {

        binding.apply {
            when (forgotPasswordBlocManagementStatus) {
                ForgotPasswordBlocManagementStatus.EMAIL -> {
                    tvForgotPasswordTitle.text = getString(R.string.forgot_password)
                    tvForgotPasswordSubTitle.text =
                        getString(R.string.please_enter_email_address_to_forgot_your_password)
                }

                ForgotPasswordBlocManagementStatus.CODE -> {
                    tvForgotPasswordTitle.text = getString(R.string.verification_code)
                    tvForgotPasswordSubTitle.text = getString(
                        R.string.please_enter_your_verification_code_to_verify_this_account_code_has_been_sent_to,
                        tieForgotPasswordEmailBlocEmail.getTextString()
                    )
                }

                else -> {
                    tvForgotPasswordTitle.text = getString(R.string.new_password)
                    tvForgotPasswordSubTitle.text =
                        getString(R.string.please_enter_your_new_password_to_access_your_account)
                }
            }
            clForgotPasswordEmailBloc.isVisible =
                forgotPasswordBlocManagementStatus == ForgotPasswordBlocManagementStatus.EMAIL
            clForgotPasswordVerificationCodeBloc.isVisible =
                forgotPasswordBlocManagementStatus == ForgotPasswordBlocManagementStatus.CODE
            clForgotPasswordNewPasswordBloc.isVisible =
                forgotPasswordBlocManagementStatus == ForgotPasswordBlocManagementStatus.NEW_PASSWORD

        }
    }

    override fun setupToolbar(binding: ActivityForgotPasswordBinding) {

    }

    override fun onClickListeners(binding: ActivityForgotPasswordBinding) {
        binding.apply {
            btForgotPasswordEmailBlocNext.setOnClickListener {
                if (tieForgotPasswordEmailBlocEmail.isEmpty()) {
                    showMessage(
                        getString(R.string.please_enter_email_to_verify_account),
                        MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                sendPasswordResetEmailApi()
            }
            tvForgotPasswordVerificationCodeResend.setOnClickListener {
                btForgotPasswordEmailBlocNext.performClick()
            }
            btForgotPasswordVerificationCodeChangeEmail.setOnClickListener {
                showConfirmationDialog(
                    "Change Email",
                    "Are you sure you have to change the email?",
                    {
                        blocManagement(ForgotPasswordBlocManagementStatus.EMAIL)
                        ovForgotPasswordVerificationCode.clear()
                    })
            }
            btForgotPasswordVerificationCodeVerify.setOnClickListener {
                if (ovForgotPasswordVerificationCode.getTextString().length != 6) {
                    showMessage("Enter 6 digit Code to verify", MESSAGE_COLOR_ERROR)
                    return@setOnClickListener
                }
                tokenValidationApi()
            }
            btForgotPasswordNewPasswordBlocSubmit.setOnClickListener {
                if (tieForgotPasswordNewPasswordBlocPassword.getTextString() == "") {
                    showMessage(getString(R.string.please_enter_password), MESSAGE_COLOR_ERROR)
                    return@setOnClickListener
                }
                if (tieForgotPasswordNewPasswordBlocPassword.getTextString() != tieForgotPasswordNewPasswordBlocConfirmPassword.getTextString()) {
                    showMessage(
                        getString(R.string.confirm_password_is_not_same_as_password),
                        MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                resetForgotPasswordApi()
            }
        }
    }

    private fun sendPasswordResetEmailApi() {
        binding.ovForgotPasswordVerificationCode.clear()
        viewModel.sendPasswordResetEmailApi<ForgotPasswordEmailResponse?>(
            ForgotPasswordRequest(binding.tieForgotPasswordEmailBlocEmail.text.toString())
        )
    }

    private fun tokenValidationApi() {
        viewModel.tokenValidationApi<LoginResponse?>(
            ForgotPasswordRequest(
                binding.tieForgotPasswordEmailBlocEmail.text.toString(),
                binding.ovForgotPasswordVerificationCode.getTextString()
            )
        )
    }

    private fun resetForgotPasswordApi() {
        viewModel.resetForgotPasswordApi<LoginResponse?>(
            ForgotPasswordRequest(
                binding.tieForgotPasswordEmailBlocEmail.text.toString(),
                binding.ovForgotPasswordVerificationCode.getTextString(),
                binding.tieForgotPasswordNewPasswordBlocPassword.getTextString(),
            )
        )
    }

    override fun liveDataObservers(binding: ActivityForgotPasswordBinding) {
        viewModel.forgotPasswordEmailResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    afterForgotPasswordEmailSuccess(it.response as ForgotPasswordEmailResponse?)
                }

                else -> {
                    binding.tieForgotPasswordEmailBlocEmail.clear()
                }
            }
        }
        viewModel.forgotPasswordCodeResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    afterForgotPasswordCodeSuccess()
                }

                else -> {
                    binding.ovForgotPasswordVerificationCode.clear()
                }
            }
        }
        viewModel.forgotPasswordNewPasswordResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    afterForgotPasswordNewPasswordSuccess()
                }

                else -> {
                    binding.tieForgotPasswordNewPasswordBlocPassword.clear()
                    binding.tieForgotPasswordNewPasswordBlocConfirmPassword.clear()
                }
            }
        }
    }

    private fun afterForgotPasswordEmailSuccess(response: ForgotPasswordEmailResponse?) {
        if (response != null) {
            blocManagement(ForgotPasswordBlocManagementStatus.CODE)
            binding.ovForgotPasswordVerificationCode.setText(
                response.resetPasswordToken ?: ""
            )
        } else {
            showMessage("Forgot Password Data is null", MESSAGE_COLOR_ERROR)
        }
    }

    private fun afterForgotPasswordCodeSuccess() {
        blocManagement(ForgotPasswordBlocManagementStatus.NEW_PASSWORD)
    }

    private fun afterForgotPasswordNewPasswordSuccess() {
        finish()
    }

    enum class ForgotPasswordBlocManagementStatus {
        EMAIL, CODE, NEW_PASSWORD
    }
}