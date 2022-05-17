package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eterra.databinding.ItemProductBinding
import com.example.eterra.models.Product
import java.io.IOException

class ProductsListAdapter(private val listener: AdapterClickListener) : ListAdapter<Product, ProductsListAdapter.ProductViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem =getItem(position)
        holder.bind(currentItem)
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvItemName.text = product.title
            binding.tvItemPrice.text = product.price
            try {
                Glide
                    .with(binding.root)
                    .load(product.image)
                    .centerCrop()
                    .into(binding.ivItemImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            binding.ibDelete.setOnClickListener {
                listener.onProductDeleteClickListener(product.id)
            }
            binding.root.setOnClickListener {
                listener.onProductClicked(product.id)
            }
        }

    }

    interface AdapterClickListener {
        fun onProductDeleteClickListener(productId: String)
        fun onProductClicked(productId: String)
    }

    class DiffCallback: DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product) =
            oldItem == newItem
    }

}