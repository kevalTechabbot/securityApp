package com.learning.android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.learning.android.databinding.ItemDashboardMenuBinding

class DashboardMenuAdapter(
    private val items: List<DashboardMenuResponse>,
    private val callback: (DashboardMenuResponse) -> Unit
) : RecyclerView.Adapter<DashboardMenuAdapter.SalesOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesOrderViewHolder {
        val binding =
            ItemDashboardMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalesOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesOrderViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class SalesOrderViewHolder(private val binding: ItemDashboardMenuBinding?) :
        RecyclerView.ViewHolder(binding!!.root) {

        fun bind(item: DashboardMenuResponse) {
            binding?.apply {
                tvItemDashboardMenuTitle.text = item.title

                Glide.with(root.context).load(item.icon)
                    .placeholder(R.mipmap.ic_launcher_foreground).into(ivItemDashboardMenuIcon)

                root.setOnClickListener {
                    callback.invoke(item)
                }

            }
        }
    }
}