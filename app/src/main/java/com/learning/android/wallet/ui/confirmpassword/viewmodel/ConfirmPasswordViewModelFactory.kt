package com.learning.android.wallet.ui.confirmpassword.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learning.android.wallet.repository.confirmpassword.ConfirmPasswordRepository

class ConfirmPasswordViewModelFactory(private val repository: ConfirmPasswordRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConfirmPasswordViewModel(repository) as T
    }
}