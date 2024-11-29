package com.learning.android.wallet.ui.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learning.android.wallet.repository.signup.SignupRepository

class SignupViewModelFactory(private val repository: SignupRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(repository) as T
    }
}