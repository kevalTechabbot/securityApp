package com.learning.android.wallet.ui.banklist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learning.android.wallet.repository.banklist.BankListRepository

class BankListViewModelFactory(private val repository: BankListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BankListViewModel(repository) as T
    }
}