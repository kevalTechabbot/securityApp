package com.learning.android.wallet.model.transactionlist.request

data class TransactionListRequest(
	val cardId: String? = null,
	val lastNTransaction: String? = null
)

