package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eterra.databinding.ItemDashboardProductBinding
import com.example.eterra.databinding.ItemProductBinding
import com.example.eterra.models.Product
import java.io.IOException

class DashboardListAdapter(private val listener: DashboardAdapterClickListener): ListAdapter<Product, DashboardListAdapter.DashboardItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemViewHolder {
        val binding = ItemDashboardProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class DashboardItemViewHolder(
        private val binding: ItemDashboardProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvDashboardItemTitle.text = product.title
            binding.tvDashboardItemPrice.text = product.price
            try {
                Glide
                    .with(binding.root)
                    .load(product.image)
                    .centerCrop()
                    .into(binding.ivDashboardItemImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            binding.root.setOnClickListener {
                listener.onProductClicked(product.id, product.user_id)
            }
        }

    }

    interface DashboardAdapterClickListener {
        fun onProductClicked(productId: String, ownerId: String)
    }

    class DiffCallback: DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product) =
            oldItem == newItem
    }


}