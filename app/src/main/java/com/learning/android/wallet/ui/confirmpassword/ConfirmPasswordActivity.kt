package com.learning.android.wallet.ui.confirmpassword

import android.app.Activity
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.learning.android.R
import com.learning.android.databinding.ActivityConfirmPasswordBinding
import com.learning.android.wallet.model.login.LoginResponse
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.confirmpassword.ConfirmPasswordRepository
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.model.confirmpassword.request.ConfirmPasswordRequest
import com.learning.android.wallet.ui.confirmpassword.viewmodel.ConfirmPasswordViewModel
import com.learning.android.wallet.ui.confirmpassword.viewmodel.ConfirmPasswordViewModelFactory
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
import com.learning.android.utils.ExtensionUtils.EditTextUtils.getTextString
import com.learning.android.utils.ExtensionUtils.EditTextUtils.onDone

class ConfirmPasswordActivity : BaseActivity<ActivityConfirmPasswordBinding>() {

    private lateinit var viewModel: ConfirmPasswordViewModel
    private lateinit var repository: ConfirmPasswordRepository

    override fun getBinding(inflater: LayoutInflater): ActivityConfirmPasswordBinding {
        return ActivityConfirmPasswordBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityConfirmPasswordBinding) {
        binding.apply {
            editTextConfirmPassword.onDone {
                buttonVerify.performClick()
            }
        }
    }

    override fun initViews(binding: ActivityConfirmPasswordBinding) {
        repository = ConfirmPasswordRepository(this)
        viewModel = ViewModelProvider(
            this, ConfirmPasswordViewModelFactory(repository)
        )[ConfirmPasswordViewModel::class.java]
    }

    override fun setupToolbar(binding: ActivityConfirmPasswordBinding) {}

    override fun onClickListeners(binding: ActivityConfirmPasswordBinding) {
        with(binding) {
            buttonVerify.setOnClickListener {
                if (editTextPassword.getTextString() == "") {
                    showMessage(getString(R.string.please_enter_password), MESSAGE_COLOR_ERROR)
                    return@setOnClickListener
                }
                if (editTextConfirmPassword.getTextString() != editTextPassword.getTextString()) {
                    showMessage(
                        getString(R.string.confirm_password_is_not_same_as_password),
                        MESSAGE_COLOR_ERROR
                    )
                    return@setOnClickListener
                }
                verifyPasswordApi()
            }
        }
    }

    override fun liveDataObservers(binding: ActivityConfirmPasswordBinding) {
        viewModel.confirmPasswordNewPasswordResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    sharedPref.putString(AppConstants.LOGIN_PASSCODE, "")
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                else -> {

                }
            }
        }
    }


    private fun verifyPasswordApi() {
        viewModel.verifyPasswordApi<LoginResponse?>(
            ConfirmPasswordRequest(
                sharedPref.getString(AppConstants.USER_ID),
                binding.editTextPassword.getTextString(),
            )
        )
    }
}