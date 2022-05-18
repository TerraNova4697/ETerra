package com.example.eterra.ui.cartlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentCartListBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.adapters.CartItemsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToLong

@AndroidEntryPoint
class CartListFragment: BaseFragment(R.layout.fragment_cart_list) {

    private lateinit var binding: FragmentCartListBinding
    private val cartListViewModel: CartListViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        cartListViewModel.collectCartItems()

        val cartItemsListAdapter = CartItemsListAdapter()

        binding.apply {
            tvShippingCharge.text = "$10.00"
            rvCartItemsList.apply {
                adapter = cartItemsListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        cartListViewModel.cartItems.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.apply {
                    rvCartItemsList.visibility = View.VISIBLE
                    tvNoProductsYet.visibility = View.GONE
                    llCheckout.visibility = View.VISIBLE
                    cartItemsListAdapter.submitList(it)
                }
            } else {
                binding.apply {
                    rvCartItemsList.visibility = View.GONE
                    tvNoProductsYet.visibility = View.VISIBLE
                    llCheckout.visibility = View.GONE
                }
            }
        }
        cartListViewModel.subtotal.observe(viewLifecycleOwner) {
            binding.tvSubTotal.text = "$${it}"
            val shipping = binding.tvShippingCharge.text.toString()
            binding.tvTotalAmount.text = "$${10.00 + it}"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartListBinding.bind(view)
    }

}