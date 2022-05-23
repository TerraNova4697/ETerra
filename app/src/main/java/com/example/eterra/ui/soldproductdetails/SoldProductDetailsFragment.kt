package com.example.eterra.ui.soldproductdetails

import android.os.Bundle
import android.view.View
import com.example.eterra.R
import com.example.eterra.databinding.FragmentSoldProductDetailsBinding
import com.example.eterra.models.SoldProduct
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
import com.example.eterra.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoldProductDetailsFragment: BaseFragment(R.layout.fragment_sold_product_details) {

    private lateinit var binding: FragmentSoldProductDetailsBinding
    private var soldProduct = SoldProduct()

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity() as SignedInActivity).bottomNavigationView.visibility = View.GONE
        (requireActivity() as SignedInActivity).supportActionBar?.hide()
        soldProduct = arguments?.getParcelable(Constants.EXTRA_SOLD_PRODUCT_DETAILS) ?: SoldProduct()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSoldProductDetailsBinding.bind(view)

    }

}