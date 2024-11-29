package com.learning.android.wallet.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learning.android.wallet.repository.login.LoginRepository

class LoginViewModelFactory(private val repository: LoginRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }
}