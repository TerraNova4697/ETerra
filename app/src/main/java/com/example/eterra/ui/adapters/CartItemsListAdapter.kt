package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eterra.R
import com.example.eterra.databinding.ItemCartLayoutBinding
import com.example.eterra.models.CartItem
import java.io.IOException

class CartItemsListAdapter(
    private val listener: CartItemsClickListeners
): ListAdapter<CartItem, CartItemsListAdapter.CartItemsViewHolder>(DiffCallback()) {

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

        fun bind(cartItem: CartItem) {
            binding.apply {
                tvCartItemTitle.text = cartItem.title
                tvCartItemPrice.text = cartItem.price
                tvCartQuantity.text = cartItem.cart_quantity

                if (tvCartQuantity.text.toString() == "0") {
                    ibAddCartItem.visibility = View.GONE
                    ibRemoveCartItem.visibility = View.GONE
                } else {
                    ibAddCartItem.visibility = View.VISIBLE
                    ibRemoveCartItem.visibility = View.VISIBLE
                }

                try {
                    Glide
                        .with(binding.root)
                        .load(cartItem.image)
                        .centerCrop()
                        .into(binding.ivCartItemImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                ibDeleteCartItem.setOnClickListener {
                    listener.onDeleteClicked(cartItem.id)
                }
            }
        }

    }

    interface CartItemsClickListeners {
        fun onDeleteClicked(cartId: String)
    }

    class DiffCallback: DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem == newItem
    }

}