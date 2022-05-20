package com.example.eterra.ui.cartlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentCartListBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.adapters.CartItemsListAdapter
import com.example.eterra.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CartListFragment: BaseFragment(R.layout.fragment_cart_list),
    CartItemsListAdapter.CartItemsClickListeners {

    private lateinit var binding: FragmentCartListBinding
    private val cartListViewModel: CartListViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        cartListViewModel.collectProductItems()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartListBinding.bind(view)
        val cartItemsListAdapter = CartItemsListAdapter(this, true)

        binding.apply {
            tvShippingCharge.text = "$10.00"
            rvCartItemsList.apply {
                adapter = cartItemsListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            btnCheckout.setOnClickListener {
                val action = CartListFragmentDirections.actionCartListFragmentToAddressListFragment(Constants.CHOOSE_ADDRESS)
                findNavController().navigate(action)
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

        lifecycleScope.launchWhenCreated {
            cartListViewModel.cartListUiEvents.collect { event ->
                when (event) {

                    is CartListViewModel.CartListUiEvent.ShowProgressBar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is CartListViewModel.CartListUiEvent.HideProgressBar -> {
                        hideProgressBar(binding.progressBar)
                    }
                    is CartListViewModel.CartListUiEvent.ShowError -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }
                }
            }
        }
    }

    override fun onDeleteClicked(cartId: String) {
        cartListViewModel.onCartItemDeleteClicked(cartId)
    }

    override fun addOneItem(cartId: String, currentCartQuantity: String, stockQuantity: String) {
        cartListViewModel.onAddOneItemClicked(cartId, currentCartQuantity, stockQuantity)
    }

    override fun removeOneItem(cartId: String, currentCartQuantity: String) {
        cartListViewModel.onRemoveOneItemClicked(cartId, currentCartQuantity)
    }

}