package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eterra.databinding.ItemProductBinding
import com.example.eterra.models.Order
import java.io.IOException

class OrdersListAdapter(private val listener: AdapterClickListener): ListAdapter<Order, OrdersListAdapter.OrderViewHolder>(DiffCallback())  {

    interface AdapterClickListener {
        fun onProductDeleteClickListener(productId: String)
        fun onProductClicked(order: Order)
    }

    inner class OrderViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvItemName.text = order.title
            binding.tvItemPrice.text = "$${order.total_amount}"
            try {
                Glide
                    .with(binding.root)
                    .load(order.image)
                    .centerCrop()
                    .into(binding.ivItemImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
//            binding.ibDelete.setOnClickListener {
//                listener.onProductDeleteClickListener(product.id)
//            }
            binding.root.setOnClickListener {
                listener.onProductClicked(order)
            }
        }

    }

    class DiffCallback: DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Order, newItem: Order) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentItem =getItem(position)
        holder.bind(currentItem)
    }

}