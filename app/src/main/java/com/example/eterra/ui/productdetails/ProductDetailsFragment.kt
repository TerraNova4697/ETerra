package com.example.eterra.ui.productdetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.eterra.R
import com.example.eterra.databinding.FragmentProductDetailsBinding
import com.example.eterra.models.Product
import com.example.eterra.ui.BaseFragment
import com.example.eterra.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.IOException

@AndroidEntryPoint
class ProductDetailsFragment(): BaseFragment(R.layout.fragment_product_details) {

    private lateinit var productId: String
    private lateinit var binding: FragmentProductDetailsBinding
    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailsBinding.bind(view)

        productId = arguments?.getString(Constants.EXTRA_PRODUCT_ID) ?: ""
        productDetailsViewModel.fetchProductDetails(productId)

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
                }
            }
        }
    }

    private fun displayDetails(product: Product) {
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