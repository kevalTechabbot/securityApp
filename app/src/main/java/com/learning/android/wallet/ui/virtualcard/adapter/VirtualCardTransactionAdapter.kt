package com.learning.android.wallet.ui.virtualcard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.learning.android.R
import com.learning.android.databinding.ItemVirtualCardTransactionBinding
import com.learning.android.wallet.model.cardandtransaction.CardAndTransactionResponse
import com.learning.android.utils.AppUtils.convertServerDateTimeToApp
import java.text.NumberFormat
import java.util.Locale

class VirtualCardTransactionAdapter(
    private val transactionList: List<CardAndTransactionResponse.TransactionsTransaction?>,
    private val callback: (CardAndTransactionResponse.TransactionsTransaction) -> Unit
) : RecyclerView.Adapter<VirtualCardTransactionAdapter.SelectCustomerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCustomerViewHolder {
        val binding = ItemVirtualCardTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectCustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCustomerViewHolder, position: Int) {
        val item = transactionList[position]
        if (item != null) holder.bind(item, position)
    }

    override fun getItemCount() = transactionList.size

    inner class SelectCustomerViewHolder(private val binding: ItemVirtualCardTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CardAndTransactionResponse.TransactionsTransaction, position: Int
        ) {
            binding.apply {
                when (item.status) {
                    3 -> {
                        ivItemVirtualCardTransactionInOut.setImageResource(R.drawable.ic_transaction_out)
                        tvItemVirtualCardTransactionAmount.setTextColor(
                            ContextCompat.getColor(
                                root.context, R.color.red_500
                            )
                        )
                    }

                    4 -> {
                        ivItemVirtualCardTransactionInOut.setImageResource(R.drawable.ic_transaction_in)
                        tvItemVirtualCardTransactionAmount.setTextColor(
                            ContextCompat.getColor(
                                root.context, R.color.green_400
                            )
                        )
                    }
                }
                tvItemVirtualCardTransactionId.text = item.paidOrReceived ?: "-"
                tvItemVirtualCardTransactionDate.text =
                    item.transactionDate.convertServerDateTimeToApp()
                try {
                    tvItemVirtualCardTransactionAmount.text = root.context.getString(
                        R.string.amount_with_symbol,
                        NumberFormat.getNumberInstance(Locale.getDefault())
                            .format(item.amount.toString().toDouble())
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    tvItemVirtualCardTransactionAmount.text = root.context.getString(
                        R.string.amount_with_symbol, "~~"
                    )
                }
                root.setOnClickListener {
                    callback.invoke(item)
                }
            }
        }
    }
}