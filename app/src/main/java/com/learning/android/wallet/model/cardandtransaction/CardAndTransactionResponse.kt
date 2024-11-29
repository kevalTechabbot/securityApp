package com.learning.android.wallet.model.cardandtransaction

import java.io.Serializable

data class CardAndTransactionResponse(
    val cvv: String? = null,
    val availableAmount: String? = null,
    val createdDate: String? = null,
    val isParent: String? = null,
    val validUpto: String? = null,
    val cardId: String? = null,
    val bankName: String? = null,
    val transactions: List<TransactionsTransaction?>? = null,
    val userId: String? = null,
    val cardNumber: String? = null
) : Serializable {
    data class TransactionsTransaction(
        val amount: String? = null,
        val cardId: String? = null,
        val status: Int? = null,
        val isParentCard: String? = null,
        val transactionDate: String? = null,
        val transactionId: String? = null,
        val bankName: String? = null,
        val cardNumber: String? = null,
        val paidOrReceived: String? = null,
    ) : Serializable
}
