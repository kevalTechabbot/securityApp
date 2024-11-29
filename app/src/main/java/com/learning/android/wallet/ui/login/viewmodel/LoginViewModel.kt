package com.learning.android.wallet.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.login.LoginRepository
import com.learning.android.wallet.ui.base.BaseViewModel
import com.learning.android.wallet.model.login.request.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel(
    val repository: LoginRepository
) : BaseViewModel() {

    val _loginUserResponse: MutableLiveData<ApiState<*>> = MutableLiveData()
    val loginUserResponse: LiveData<ApiState<*>>
        get() {
            return _loginUserResponse
        }

    inline fun <reified T> loginApi(loginRequest: LoginRequest) {
        viewModelScope.launch {
            val response = repository.loginApi<T>(loginRequest)
            launch {
                _loginUserResponse.postValue(response)
            }
        }
    }
}