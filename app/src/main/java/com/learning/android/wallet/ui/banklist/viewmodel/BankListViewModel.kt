package com.learning.android.wallet.ui.banklist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.banklist.BankListRepository
import com.learning.android.wallet.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BankListViewModel(
    val repository: BankListRepository
) : BaseViewModel() {
    val _bankListResponse: MutableLiveData<ApiState<*>> = MutableLiveData()
    val bankListResponse: LiveData<ApiState<*>>
        get() {
            return _bankListResponse
        }

    inline fun <reified T> getBankNamesApi() {
        viewModelScope.launch {
            val response = repository.getBankNamesApi<T>()
            launch {
                _bankListResponse.postValue(response)
            }
        }
    }
}