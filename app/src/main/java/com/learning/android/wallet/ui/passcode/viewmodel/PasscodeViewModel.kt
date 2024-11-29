package com.learning.android.wallet.ui.passcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.passcode.PasscodeRepository
import com.learning.android.wallet.ui.base.BaseViewModel
import com.learning.android.wallet.model.passcode.request.MPinAddRequest
import com.learning.android.wallet.model.passcode.request.MPinResetRequest
import com.learning.android.wallet.model.passcode.request.MPinVerifyRequest
import kotlinx.coroutines.launch

class PasscodeViewModel(
    val repository: PasscodeRepository
) : BaseViewModel() {
    val _mPinAddResponse: MutableLiveData<ApiState<*>> = MutableLiveData()
    val mPinAddResponse: LiveData<ApiState<*>>
        get() {
            return _mPinAddResponse
        }
    val _mPinResetResponse: MutableLiveData<ApiState<*>> = MutableLiveData()
    val mPinResetResponse: LiveData<ApiState<*>>
        get() {
            return _mPinResetResponse
        }
    val _mPinVerifyResponse: MutableLiveData<ApiState<*>> = MutableLiveData()
    val mPinVerifyResponse: LiveData<ApiState<*>>
        get() {
            return _mPinVerifyResponse
        }
    val _biometricsVerifyResponse: MutableLiveData<ApiState<*>> = MutableLiveData()
    val biometricsVerifyResponse: LiveData<ApiState<*>>
        get() {
            return _biometricsVerifyResponse
        }

    inline fun <reified T> createMPinApi(mPinAddRequest: MPinAddRequest) {
        viewModelScope.launch {
            val response = repository.createMPinApi<T>(mPinAddRequest)
            launch {
                _mPinAddResponse.postValue(response)
            }
        }
    }

    inline fun <reified T> resetMPinApi(mPinResetRequest: MPinResetRequest) {
        viewModelScope.launch {
            val response = repository.resetMPinApi<T>(mPinResetRequest)
            launch {
                _mPinResetResponse.postValue(response)
            }
        }
    }

    inline fun <reified T> verifyMPinApi(mPinVerifyRequest: MPinVerifyRequest) {
        viewModelScope.launch {
            val response = repository.verifyMPinApi<T>(mPinVerifyRequest)
            launch {
                _mPinVerifyResponse.postValue(response)
            }
        }
    }

    inline fun <reified T> biometricsVerificationApi() {
        viewModelScope.launch {
            val response = repository.biometricsVerificationApi<T>()
            launch {
                _biometricsVerifyResponse.postValue(response)
            }
        }
    }

}