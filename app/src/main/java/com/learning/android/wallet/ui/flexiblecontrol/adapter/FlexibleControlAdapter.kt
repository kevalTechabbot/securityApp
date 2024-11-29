package com.learning.android.wallet.ui.flexiblecontrol.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.android.R
import com.learning.android.databinding.ItemFlexibleControlBinding
import com.learning.android.wallet.model.cardandtransaction.CardAndTransactionResponse
import com.learning.android.wallet.ui.cardtransactiondetail.CardTransactionDetailActivity
import com.learning.android.wallet.ui.virtualcard.adapter.VirtualCardTransactionAdapter
import com.learning.android.utils.AppUtils.convertCardNumberToStar
import java.text.NumberFormat
import java.util.Locale

class FlexibleControlAdapter(
    private val cardList: List<CardAndTransactionResponse?>,
    private val callback: (CardAndTransactionResponse) -> Unit
) : RecyclerView.Adapter<FlexibleControlAdapter.SelectCustomerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCustomerViewHolder {
        val binding = ItemFlexibleControlBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectCustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCustomerViewHolder, position: Int) {
        val item = cardList[position]
        if (item != null) holder.bind(item, position)
    }

    override fun getItemCount() = cardList.size

    inner class SelectCustomerViewHolder(private val binding: ItemFlexibleControlBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CardAndTransactionResponse, position: Int) {
            binding.apply {
                try {
                    tvItemFlexibleControlAmount.text = root.context.getString(
                        R.string.amount_with_symbol,
                        NumberFormat.getNumberInstance(Locale.getDefault())
                            .format(item.availableAmount.toString().toDouble())
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    tvItemFlexibleControlAmount.text = root.context.getString(
                        R.string.amount_with_symbol, "~~"
                    )
                }
                tvItemFlexibleControlAccountNumber.text = item.cardNumber?.convertCardNumberToStar()
                tvItemFlexibleControlBankName.text = item.bankName
                val transactionList = item.transactions
                if (transactionList != null) rvItemFlexibleControlTransactionList.adapter =
                    VirtualCardTransactionAdapter(transactionList) { selectedData ->
                        root.context.startActivity(
                            Intent(
                                root.context, CardTransactionDetailActivity::class.java
                            ).putExtra("selectedData", selectedData).setAction(
                                "your.custom.action"
                            )
                        )
                    }
                root.setOnClickListener {
                    callback.invoke(item)
                }
            }
        }
    }
}