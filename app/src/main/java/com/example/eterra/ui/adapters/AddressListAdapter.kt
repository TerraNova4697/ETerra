package com.example.eterra.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eterra.databinding.ItemAddressLayoutBinding
import com.example.eterra.models.Address

class AddressListAdapter(private val listener: OnItemClickedListener) :
    ListAdapter<Address, AddressListAdapter.AddressViewHolder>(DiffCallback()) {

    interface OnItemClickedListener {
        fun onAddressSelected(address: Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding =
            ItemAddressLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class AddressViewHolder(
        private val binding: ItemAddressLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Address) {
            binding.tvAddressFullName.text = item.name
            binding.tvAddressDetails.text = item.address
            binding.tvAddressMobileNumber.text = item.mobileNumber
            binding.tvAddressType.text = item.type
            binding.root.setOnClickListener {
                listener.onAddressSelected(item)
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Address, newItem: Address) =
            oldItem == newItem
    }

}