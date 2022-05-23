package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eterra.databinding.ItemProductBinding
import com.example.eterra.models.Product
import com.example.eterra.models.SoldProduct
import java.io.IOException

class SoldProductsListAdapter(private val listener: AdapterClickListener): ListAdapter<SoldProduct, SoldProductsListAdapter.SoldProductViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoldProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoldProductViewHolder, position: Int) {
        val currentItem =getItem(position)
        holder.bind(currentItem)
    }

    inner class SoldProductViewHolder(
        private val binding: ItemProductBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(soldProduct: SoldProduct) {
            binding.tvItemName.text = soldProduct.title
            binding.tvItemPrice.text = soldProduct.price
            try {
                Glide
                    .with(binding.root)
                    .load(soldProduct.image)
                    .centerCrop()
                    .into(binding.ivItemImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            binding.ibDelete.visibility = View.GONE
            binding.root.setOnClickListener {
                listener.onProductClicked(soldProduct)
            }
        }
    }

    interface AdapterClickListener {
        fun onProductClicked(soldProduct: SoldProduct)
    }

    class DiffCallback: DiffUtil.ItemCallback<SoldProduct>() {
        override fun areItemsTheSame(oldItem: SoldProduct, newItem: SoldProduct) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SoldProduct, newItem: SoldProduct) =
            oldItem == newItem
    }

}