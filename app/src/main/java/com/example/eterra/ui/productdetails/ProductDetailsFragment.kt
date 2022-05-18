package com.example.eterra.ui.productdetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.eterra.R
import com.example.eterra.databinding.FragmentProductDetailsBinding
import com.example.eterra.models.Product
import com.example.eterra.ui.BaseFragment
import com.example.eterra.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.IOException

@AndroidEntryPoint
class ProductDetailsFragment: BaseFragment(R.layout.fragment_product_details) {

    private lateinit var productId: String
    private lateinit var ownerId: String
    private lateinit var binding: FragmentProductDetailsBinding
    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()
    private lateinit var productDetails: Product

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailsBinding.bind(view)

        productId = arguments?.getString(Constants.EXTRA_PRODUCT_ID) ?: ""
        ownerId = arguments?.getString(Constants.EXTRA_PRODUCT_OWNER_ID) ?: ""
        productDetailsViewModel.fetchProductDetails(productId)

        if (ownerId == FirebaseAuth.getInstance().currentUser!!.uid) {
            hideBtns()
        } else {
            showBtns()
        }

        binding.apply {
            btnAddToCard.setOnClickListener {
                productDetailsViewModel.onAddToCartClicked(productDetails, productId)
            }
            btnGoToCart.setOnClickListener {
                val action = ProductDetailsFragmentDirections.actionProductDetailsFragmentToCartListFragment()
                findNavController().navigate(action)
            }
        }

        lifecycleScope.launchWhenCreated {
            productDetailsViewModel.productDetailsUiEvents.collect { event ->
                when (event) {
                    is ProductDetailsViewModel.ProductDetailsUiEvent.ShowProgressBar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is ProductDetailsViewModel.ProductDetailsUiEvent.HideProgressBar -> {
                        hideProgressBar(binding.progressBar)
                    }
                    is ProductDetailsViewModel.ProductDetailsUiEvent.DisplayDetails -> {
                        displayDetails(event.product)
                    }
                    is ProductDetailsViewModel.ProductDetailsUiEvent.AddToCartSuccess -> {
                        showErrorSnackBar(resources.getString(R.string.product_added_to_cart), false)
                    }
                    is ProductDetailsViewModel.ProductDetailsUiEvent.AddToCartFailure -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }
                }
            }
        }
    }

    private fun showBtns() {
        binding.btnAddToCard.visibility = View.VISIBLE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    private fun hideBtns() {
        binding.btnAddToCard.visibility = View.GONE
        binding.btnGoToCart.visibility = View.GONE
    }

    private fun displayDetails(product: Product) {
        productDetails = product
        binding.apply {
            try {
                Glide
                    .with(binding.root)
                    .load(product.image)
                    .centerCrop()
                    .into(binding.ivProductDetailImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            tvProductDetailsTitle.text = product.title
            tvProductDetailsPrice.text = product.price
            tvProductDetailsDescription.text = product.description
            tvProductDetailsAvailableQuantity.text = product.quantity
        }
    }

}