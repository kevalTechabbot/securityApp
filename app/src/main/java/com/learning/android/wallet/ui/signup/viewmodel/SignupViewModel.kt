package com.learning.android.wallet.ui.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.signup.SignupRepository
import com.learning.android.wallet.ui.base.BaseViewModel
import com.learning.android.wallet.model.signup.request.SignupRequest
import kotlinx.coroutines.launch

class SignupViewModel(
    val repository: SignupRepository
) : BaseViewModel() {

    val _signUpUserResponse: MutableLiveData<ApiState<*>> = MutableLiveData()
    val signUpUserResponse: LiveData<ApiState<*>>
        get() {
            return _signUpUserResponse
        }

    inline fun <reified T> signupApi(signupRequest: SignupRequest) {
        viewModelScope.launch {
            val response = repository.signupApi<T>(signupRequest)
            launch {
                _signUpUserResponse.postValue(response)
            }
        }
    }
}