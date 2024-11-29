package com.learning.android.wallet.ui.virtualcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learning.android.wallet.repository.virtualcard.VirtualCardRepository

class VirtualCardViewModelFactory(
    private val repository: VirtualCardRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VirtualCardViewModel(repository) as T
    }
}