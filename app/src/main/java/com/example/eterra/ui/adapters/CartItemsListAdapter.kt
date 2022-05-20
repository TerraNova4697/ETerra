package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eterra.databinding.ItemCartLayoutBinding
import com.example.eterra.models.CartItem
import java.io.IOException

class CartItemsListAdapter(
    private val listener: CartItemsClickListeners,
    private val updateCartItems: Boolean
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

                if (updateCartItems) {
                    ibAddCartItem.visibility = View.VISIBLE
                    ibRemoveCartItem.visibility = View.VISIBLE
                    ibDeleteCartItem.visibility = View.VISIBLE
                } else {
                    ibAddCartItem.visibility = View.GONE
                    ibRemoveCartItem.visibility = View.GONE
                    ibDeleteCartItem.visibility = View.GONE
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
                ibAddCartItem.setOnClickListener {
                    listener.addOneItem(cartItem.id, cartItem.cart_quantity, cartItem.stock_quantity)
                }
                ibRemoveCartItem.setOnClickListener {
                    listener.removeOneItem(cartItem.id, cartItem.cart_quantity)
                }
            }
        }

    }

    interface CartItemsClickListeners {
        fun onDeleteClicked(cartId: String)
        fun addOneItem(cartId: String, currentCartQuantity: String, stockQuantity: String)
        fun removeOneItem(cartId: String, currentCartQuantity: String)
    }

    class DiffCallback: DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem == newItem
    }

}