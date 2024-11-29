package com.learning.android.wallet.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.android.R
import com.learning.android.databinding.ItemDashboardBinding

class DashboardAdapter(
    allItems: List<DashboardItemData>,
    private val callback: (Int) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.SelectCustomerViewHolder>() {

    private var items: List<DashboardItemData> = allItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCustomerViewHolder {
        val binding = ItemDashboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectCustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectCustomerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = items.size

    inner class SelectCustomerViewHolder(private val binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashboardItemData, position: Int) {
            binding.apply {
                ivItemDashboardLeading.setImageResource(item.icon)
                if (item.next != null)
                    ivItemDashboardNext.setImageResource(item.next)
                else
                    ivItemDashboardNext.setImageResource(R.drawable.ic_right_arrow)
                tvItemDashboardTitle.text = item.title
                root.setOnClickListener {
                    callback.invoke(position)
                }
            }
        }
    }
}