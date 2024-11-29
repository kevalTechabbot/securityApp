package com.learning.android.wallet.repository.virtualcard

import android.content.Context
import com.learning.android.wallet.network.ApiState
import com.learning.android.wallet.repository.RepositoryBase
import com.learning.android.wallet.model.transactionlist.request.TransactionListRequest
import com.learning.android.utils.AppUtils

class VirtualCardRepository(val context: Context) : RepositoryBase(context) {
    // Card and Transaction List API
    suspend inline fun <reified T> getCardAndTransactionApi(
        isSuccessMessageShow: Boolean = false,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.getCardAndTransactionApi() }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }

    // All Transaction List API
    suspend inline fun <reified T> getLastNTransactionApi(
        transactionListRequest: TransactionListRequest,
        isSuccessMessageShow: Boolean = false,
        isFailureMessageShow: Boolean = true,
    ): ApiState<T> {
        val apiCall = { apiClient.getLastNTransactionApi(transactionListRequest) }

        return AppUtils.getDataFromDatabaseOrApiLogic<T>(
            context,
            apiCall,
            isFailureMessageShow,
            isSuccessMessageShow,
        )
    }
}