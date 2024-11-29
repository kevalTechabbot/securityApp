package com.learning.android.wallet.ui.passcode.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.learning.android.R
import com.learning.android.databinding.ItemPasscodeFilledCodeBinding

class PasscodeFilledAdapter(
    allItems: List<String>,
) : RecyclerView.Adapter<PasscodeFilledAdapter.SelectCustomerViewHolder>() {

    private var items: List<String> = allItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCustomerViewHolder {
        val binding = ItemPasscodeFilledCodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectCustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCustomerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class SelectCustomerViewHolder(private val binding: ItemPasscodeFilledCodeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.apply {
                if (item == "") {
                    ivItemPasscodeFilledCode.setImageDrawable(
                        ContextCompat.getDrawable(
                            ivItemPasscodeFilledCode.context,
                            R.drawable.circular_border_transparent
                        )
                    )
                } else {
                    ivItemPasscodeFilledCode.setImageDrawable(
                        ContextCompat.getDrawable(
                            ivItemPasscodeFilledCode.context,
                            R.drawable.circular_solid_white
                        )
                    )
                }
            }
        }
    }
}