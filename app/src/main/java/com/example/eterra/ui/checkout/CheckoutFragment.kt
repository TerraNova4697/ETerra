package com.example.eterra.ui.checkout

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentCheckoutBinding
import com.example.eterra.models.Address
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.adapters.CartItemsListAdapter
import com.example.eterra.ui.products.ProductsViewModel
import com.example.eterra.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CheckoutFragment: BaseFragment(R.layout.fragment_checkout),
    CartItemsListAdapter.CartItemsClickListeners {

    private var address: Address? = null
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private lateinit var binding: FragmentCheckoutBinding

    override fun onResume() {
        super.onResume()
        checkoutViewModel.collectProducts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckoutBinding.bind(view)
        val cartItemsListAdapter = CartItemsListAdapter(this, false)

        address = arguments?.getParcelable(Constants.EXTRA_SELECTED_ADDRESS)


        binding.apply {
//            rvCartListItems.adapter = cartItemsListAdapter
            rvCartListItems.layoutManager = LinearLayoutManager(requireContext())
            rvCartListItems.setHasFixedSize(true)
        }

        if (address != null) {
            binding.apply {
                tvCheckoutAddressType.text = address!!.type
                tvCheckoutFullName.text = address!!.name
                tvCheckoutAddress.text = "${address!!.address}, ${address!!.zipCode}"
                tvCheckoutAdditionalNote.text = address!!.additionalNote
                tvMobileNumber.text = address!!.mobileNumber
            }
        }

        checkoutViewModel.cartItems.observe(viewLifecycleOwner) {
            cartItemsListAdapter.submitList(it)
            binding.rvCartListItems.adapter = cartItemsListAdapter
            for (item in it) {
                Log.i(this@CheckoutFragment.javaClass.simpleName, item.toString())
            }
        }

        lifecycleScope.launchWhenCreated {
            checkoutViewModel.checkoutViewModelEvents.collect { event ->
                when (event) {
                    is CheckoutViewModel.CheckoutUiEvent.ErrorFetchingProducts -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }
                    is CheckoutViewModel.CheckoutUiEvent.ShowProgressBar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is CheckoutViewModel.CheckoutUiEvent.HideProgressBar -> {
                        hideProgressBar(binding.progressBar)
                    }
                }
            }
        }
    }

    override fun onDeleteClicked(cartId: String) {

    }

    override fun addOneItem(cartId: String, currentCartQuantity: String, stockQuantity: String) {

    }

    override fun removeOneItem(cartId: String, currentCartQuantity: String) {

    }

}