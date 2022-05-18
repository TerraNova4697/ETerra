package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eterra.databinding.ItemCartLayoutBinding
import com.example.eterra.models.CartItem
import com.example.eterra.models.Product
import java.io.IOException

class CartItemsListAdapter(): ListAdapter<CartItem, CartItemsListAdapter.CartItemsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsViewHolder {
        val binding = ItemCartLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CartItemsViewHolder(
        private val binding: ItemCartLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: CartItem) {
            binding.apply {
                tvCartItemTitle.text = product.title
                tvCartItemPrice.text = product.price
                tvCartQuantity.text = product.cart_quantity
                try {
                    Glide
                        .with(binding.root)
                        .load(product.image)
                        .centerCrop()
                        .into(binding.ivCartItemImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    }

    class DiffCallback: DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem == newItem
    }

}