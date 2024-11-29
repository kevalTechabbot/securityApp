package com.learning.android.wallet.ui.banklist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.android.databinding.ItemBankListBinding
import com.learning.android.wallet.model.banklist.BankListResponse

class BankListAdapter(
    allItems: List<BankListResponse>, private val callback: (BankListResponse, Int) -> Unit
) : RecyclerView.Adapter<BankListAdapter.SelectCustomerViewHolder>() {

    private var items: List<BankListResponse> = allItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCustomerViewHolder {
        val binding = ItemBankListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectCustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCustomerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = items.size

    inner class SelectCustomerViewHolder(private val binding: ItemBankListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BankListResponse, position: Int) {
            binding.apply {
                tvItemBankListAppName.text = item.bankName ?: "-"
                tvItemBankListAppPackageId.text = item.packageName ?: "-"
                tvItemBankListAlphabet.text = tvItemBankListAppName.text.toString().first().toString()
                root.setOnClickListener {
                    callback.invoke(item, position)
                }
            }
        }
    }
}