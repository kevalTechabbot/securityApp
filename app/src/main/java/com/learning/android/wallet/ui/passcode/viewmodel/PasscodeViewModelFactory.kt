package com.learning.android.wallet.ui.passcode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learning.android.wallet.repository.passcode.PasscodeRepository

class PasscodeViewModelFactory(private val repository: PasscodeRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PasscodeViewModel(repository) as T
    }
}