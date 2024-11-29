package com.learning.android.wallet.ui.passcode

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.learning.android.R
import com.learning.android.databinding.ActivityPasscodeBinding
import com.learning.android.wallet.model.login.LoginResponse
import com.learning.android.wallet.model.passcode.request.MPinAddRequest
import com.learning.android.wallet.model.passcode.request.MPinResetRequest
import com.learning.android.wallet.model.passcode.request.MPinVerifyRequest
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.passcode.PasscodeRepository
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.ui.confirmpassword.ConfirmPasswordActivity
import com.learning.android.wallet.ui.dashboard.DashboardActivity
import com.learning.android.wallet.ui.passcode.adapter.PasscodeFilledAdapter
import com.learning.android.wallet.ui.passcode.adapter.PasscodeNumberAdapter
import com.learning.android.wallet.ui.passcode.viewmodel.PasscodeViewModel
import com.learning.android.wallet.ui.passcode.viewmodel.PasscodeViewModelFactory
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppConstants.LOGIN_PASSCODE
import com.learning.android.utils.AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
import com.learning.android.utils.AppUtils.doLogout

class PasscodeActivity : BaseActivity<ActivityPasscodeBinding>() {

    private lateinit var viewModel: PasscodeViewModel
    private lateinit var repository: PasscodeRepository
    private var passcode = arrayListOf("", "", "", "")
    private var newPasscode = ""
    private var newConfirmPasscode = ""
    private var passcodeNumber =
        arrayListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", null, "0", null)
    private val mPinStatus: Int? by lazy {
        intent.getIntExtra(AppConstants.MPinActionStatus.STATUS_KEY, 0)
    }
    private var isCurrentPasscodeApproved = false

    override fun getBinding(inflater: LayoutInflater): ActivityPasscodeBinding {
        return ActivityPasscodeBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityPasscodeBinding) {

    }

    override fun initViews(binding: ActivityPasscodeBinding) {
        repository = PasscodeRepository(this)
        viewModel = ViewModelProvider(
            this, PasscodeViewModelFactory(repository)
        )[PasscodeViewModel::class.java]
        initBiometric(showSuccessMsg = false) {
            if (it == null) {
                binding.tvOr.isVisible = false
                binding.ivPasscodeBiometric.isVisible = false
            } else {
                if (it) biometricsVerificationApi()
            }
        }

        if (sharedPref.getBoolean(AppConstants.UNAUTHORISED_STATUS)) {
            binding.tvOr.isVisible = false
            binding.ivPasscodeBiometric.isVisible = false
        }

        setInitialTextAndConditionForDifferentStatus()
        setPasscodeFilled(binding)
        setPasscodeNumber(binding)
    }

    private fun createMPinApi() {
        binding.apply {
            viewModel.createMPinApi<LoginResponse?>(
                MPinAddRequest(
                    passcode.joinToString(separator = ""),
                    sharedPref.getString(AppConstants.USER_ID)
                )
            )
        }
    }

    private fun resetMPinApi() {
        binding.apply {
            viewModel.resetMPinApi<LoginResponse?>(
                MPinResetRequest(
                    passcode.joinToString(separator = ""),
                    sharedPref.getString(LOGIN_PASSCODE).replace(",", "").replace(" ", "").trim(),
                    sharedPref.getString(AppConstants.USER_ID)
                )
            )
        }
    }

    private fun verifyMPinApi() {
        binding.apply {
            viewModel.verifyMPinApi<LoginResponse?>(
                MPinVerifyRequest(
                    passcode.joinToString(separator = ""),
                    sharedPref.getString(AppConstants.USER_ID)
                )
            )
        }
    }

    private fun biometricsVerificationApi() {
        binding.apply {
            viewModel.biometricsVerificationApi<LoginResponse?>()
        }
    }

    private fun setInitialTextAndConditionForDifferentStatus() {
        binding.apply {
            if (sharedPref.getString(LOGIN_PASSCODE) == "") {
                tvPasscodeTitle.text = getString(R.string.set_up_new_mpin)
                tvPasscodeForgotMPin.isVisible = false
                tvPasscodeForgotMPinView.isVisible = false
                tvOr.isVisible = false
                ivPasscodeBiometric.isVisible = false
            } else if (mPinStatus == AppConstants.MPinActionStatus.RESET) {
                tvPasscodeTitle.text = getString(R.string.enter_your_current_mpin)
                tvPasscodeForgotMPin.isVisible = false
                tvPasscodeForgotMPinView.isVisible = false
                tvOr.isVisible = false
                ivPasscodeBiometric.isVisible = false
            } else {
                tvPasscodeTitle.text = getString(R.string.enter_your_mpin)
            }
        }
    }

    private fun setPasscodeFilled(binding: ActivityPasscodeBinding) {
        binding.apply {
            rvPasscodeFilled.adapter = PasscodeFilledAdapter(passcode)
        }
    }

    private fun setPasscodeNumber(binding: ActivityPasscodeBinding) {
        binding.apply {
            val adapter = PasscodeNumberAdapter(passcodeNumber) { position, selected, status ->
                itemClickListener(position, selected, status)
            }
            rvPasscodeNumber.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun itemClickListener(position: Int?, selected: String, status: Int) {
        binding.apply {
            if (status == AppConstants.MpinKeyStatus.NEXT) {
                nextButtonLogic()
            } else if (status == AppConstants.MpinKeyStatus.TYPE && selected != "") {
                if (passcode[0] == "") {
                    passcode[0] = selected
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                } else if (passcode[1] == "") {
                    passcode[1] = selected
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                } else if (passcode[2] == "") {
                    passcode[2] = selected
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                } else if (passcode[3] == "") {
                    passcode[3] = selected
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                    if (newPasscode != "") {
                        newConfirmPasscode = passcode.joinToString()
                    } else {
                        newPasscode = passcode.joinToString()
                    }
                    nextButtonLogic()
                }
            } else if (status == AppConstants.MpinKeyStatus.DELETE) {
                if (passcode[3] != "") {
                    passcode[3] = ""
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                } else if (passcode[2] != "") {
                    passcode[2] = ""
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                } else if (passcode[1] != "") {
                    passcode[1] = ""
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                } else if (passcode[0] != "") {
                    passcode[0] = ""
                    rvPasscodeFilled.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun nextButtonLogic() {
        binding.apply {
            if (newPasscode.length < 4) {
                showMessage(
                    getString(R.string.passcode_length_has_to_be_4),
                    AppConstants.MessageBackgroundColor.MESSAGE_COLOR_NOTICE
                )
            } else if (mPinStatus == AppConstants.MPinActionStatus.RESET) {
                if (isCurrentPasscodeApproved) {
                    if (newPasscode == newConfirmPasscode) {
                        resetMPinApi()
                    } else if (newConfirmPasscode != "") {
                        resetData()
                        if (mPinStatus == AppConstants.MPinActionStatus.RESET) {
                            tvPasscodeTitle.text = getString(R.string.reset_mpin)
                        } else tvPasscodeTitle.text = getString(R.string.set_up_new_mpin)
                        showMessage(
                            getString(R.string.confirm_passcode_not_match), MESSAGE_COLOR_ERROR
                        )
                    } else {
                        resetData(resetPasscode = false)
                        tvPasscodeTitle.text = getString(R.string.confirm_mpin)
                    }
                } else {
                    verifyMPinApi()
                }
            } else if (mPinStatus == AppConstants.MPinActionStatus.LOGIN || sharedPref.getString(
                    LOGIN_PASSCODE
                ) == ""
            ) {
                if (newPasscode == newConfirmPasscode) {
                    createMPinApi()
                } else if (newConfirmPasscode != "") {
                    resetData()
                    if (mPinStatus == AppConstants.MPinActionStatus.RESET) {
                        tvPasscodeTitle.text = getString(R.string.reset_mpin)
                    } else tvPasscodeTitle.text = getString(R.string.set_up_new_mpin)
                    showMessage(
                        getString(R.string.confirm_passcode_not_match), MESSAGE_COLOR_ERROR
                    )
                } else {
                    resetData(resetPasscode = false)
                    tvPasscodeTitle.text = getString(R.string.confirm_mpin)
                }
            } else if (mPinStatus == AppConstants.MPinActionStatus.SPLASH) {
                verifyMPinApi()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetData(resetPasscode: Boolean = true) {
        binding.apply {
            if (resetPasscode) {
                newPasscode = ""
                newConfirmPasscode = ""
            }
            passcode[0] = ""
            passcode[1] = ""
            passcode[2] = ""
            passcode[3] = ""
            rvPasscodeFilled.adapter?.notifyDataSetChanged()
        }
    }

    override fun setupToolbar(binding: ActivityPasscodeBinding) {}

    override fun onClickListeners(binding: ActivityPasscodeBinding) {
        binding.apply {
            ivPasscodeBiometric.setOnClickListener {
                authenticateWithBiometrics()
            }
            tvPasscodeLogout.setOnClickListener {
                doLogout()
            }
            tvPasscodeForgotMPin.setOnClickListener {
                launcherForgotPassword.launch(
                    Intent(
                        this@PasscodeActivity, ConfirmPasswordActivity::class.java
                    )
                )
            }
        }
    }

    private var launcherForgotPassword = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            setInitialTextAndConditionForDifferentStatus()
        }
    }

    override fun liveDataObservers(binding: ActivityPasscodeBinding) {
        viewModel.mPinAddResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    sharedPref.putBoolean(AppConstants.UNAUTHORISED_STATUS, false)
                    afterLogin(it.response as LoginResponse?)
                }

                else -> {
                    binding.tvPasscodeTitle.text = getString(R.string.set_up_new_mpin)
                    resetData()
                }
            }
        }

        viewModel.mPinResetResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    sharedPref.putBoolean(AppConstants.UNAUTHORISED_STATUS, false)
                    afterLogin(it.response as LoginResponse?)
                }

                else -> {
                    binding.tvPasscodeTitle.text = getString(R.string.reset_mpin)
                    resetData()
                }
            }
        }

        viewModel.mPinVerifyResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    sharedPref.putBoolean(AppConstants.UNAUTHORISED_STATUS, false)
                    if (mPinStatus == AppConstants.MPinActionStatus.SPLASH) {
                        afterLogin(it.response as LoginResponse?)
                    } else {
                        binding.tvPasscodeTitle.text = getString(R.string.reset_mpin)
                        resetData()
                        isCurrentPasscodeApproved = true
                    }
                }

                else -> {
                    if (mPinStatus == AppConstants.MPinActionStatus.RESET) {
                        binding.tvPasscodeTitle.text = getString(R.string.enter_your_current_mpin)
                    }
                    resetData()
                }
            }
        }

        viewModel.biometricsVerifyResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    sharedPref.putBoolean(AppConstants.UNAUTHORISED_STATUS, false)
                    afterLogin(it.response as LoginResponse?, isFromBiometric = true)
                }

                else -> {
                    resetData()
                }
            }
        }
    }

    private fun afterLogin(response: LoginResponse?, isFromBiometric: Boolean = false) {
        if (response != null) {
            sharedPref.putString(AppConstants.TOKEN, response.jwt.toString())
            afterPasscodeSet(isFromBiometric)
        } else {
            showMessage("Login Data is null", MESSAGE_COLOR_ERROR)
            resetData()
        }
    }

    private fun afterPasscodeSet(isFromBiometric: Boolean = false) {
        if (mPinStatus == AppConstants.MPinActionStatus.RESET || !isFromBiometric)
            sharedPref.putString(LOGIN_PASSCODE, passcode.joinToString())
        startActivity(
            Intent(
                this@PasscodeActivity, DashboardActivity::class.java
            ).setAction("your.custom.action")
        )
        finish()
    }
}