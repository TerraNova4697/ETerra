package com.example.eterra.ui.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentCheckoutBinding
import com.example.eterra.models.Address
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as SignedInActivity).supportActionBar?.hide()
        (requireActivity() as SignedInActivity).bottomNavigationView.visibility = View.GONE

        return super.onCreateView(inflater, container, savedInstanceState)
    }

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
            binding.apply {
                btnPlaceOrder.setOnClickListener {
                    if (address != null) {
                        checkoutViewModel.onPlaceOrderClicked(address!!)
                    }
                }
            }
        }

        if (address != null) {
            binding.apply {
                tvCheckoutAddressType.text = address!!.type
                tvCheckoutFullName.text = address!!.name
                tvCheckoutAddress.text = "${address!!.address}, ${address!!.zipCode}"
                tvCheckoutMobileNumber.text = address!!.mobileNumber
                tvCheckoutAdditionalNote.text = address!!.additionalNote
                tvCheckoutOtherDetails.text = address!!.otherDetails
            }
        }

        checkoutViewModel.cartItems.observe(viewLifecycleOwner) {
            cartItemsListAdapter.submitList(it)
            binding.rvCartListItems.adapter = cartItemsListAdapter
            for (item in it) {
                Log.i(this@CheckoutFragment.javaClass.simpleName, item.toString())
            }
        }

        checkoutViewModel.subtotal.observe(viewLifecycleOwner) {
            binding.apply {tvCheckoutSubTotal.text = "$${it.toString()}"}
        }

        checkoutViewModel.shippingCharge.observe(viewLifecycleOwner) {
            binding.apply { tvCheckoutShippingCharge.text = "$${it.toString()}" }
        }

        checkoutViewModel.total.observe(viewLifecycleOwner) {
            binding.apply { tvCheckoutTotalAmount.text = "$${it.toString()}" }
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
                    is CheckoutViewModel.CheckoutUiEvent.NotifyUser -> {
                        showErrorSnackBar(event.message, false)
                    }
                    is CheckoutViewModel.CheckoutUiEvent.NavigateToDashboard -> {
                        val action = CheckoutFragmentDirections.actionCheckoutFragmentToItemDashboard()
                        findNavController().navigate(action)
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