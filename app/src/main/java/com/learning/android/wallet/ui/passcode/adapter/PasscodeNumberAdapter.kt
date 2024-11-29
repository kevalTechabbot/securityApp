package com.learning.android.wallet.ui.passcode.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.learning.android.databinding.ItemPasscodeCodeBinding
import com.learning.android.utils.AppConstants

class PasscodeNumberAdapter(
    allItems: List<String?>, private val callback: (Int?, String, Int) -> Unit
) : RecyclerView.Adapter<PasscodeNumberAdapter.SelectCustomerViewHolder>() {

    private var items: List<String?> = allItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCustomerViewHolder {
        val binding = ItemPasscodeCodeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectCustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCustomerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = items.size

    inner class SelectCustomerViewHolder(private val binding: ItemPasscodeCodeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String?, position: Int) {
            binding.apply {
                if (item == null) {
                    if (position == 9) {
                        tvItemPasscodeNumber.isVisible = false
                    } else {
                        tvItemPasscodeNumber.isVisible = false
                        ivItemPasscodeBack.isVisible = true
                        ivItemPasscodeBack.setOnClickListener {
                            callback(
                                null,
                                "",
                                AppConstants.MpinKeyStatus.DELETE
                            )
                        }
                    }
                } else {
                    tvItemPasscodeNumber.isVisible = true
                    tvItemPasscodeNumber.text = item
                    ivItemPasscodeBack.isVisible = false
                    tvItemPasscodeNumber.setOnClickListener {
                        callback(
                            position,
                            tvItemPasscodeNumber.text.toString(),
                            AppConstants.MpinKeyStatus.TYPE
                        )
                    }
                }
            }
        }
    }
}