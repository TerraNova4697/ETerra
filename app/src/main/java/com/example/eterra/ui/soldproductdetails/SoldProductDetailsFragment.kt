package com.example.eterra.ui.soldproductdetails

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.eterra.R
import com.example.eterra.databinding.FragmentSoldProductDetailsBinding
import com.example.eterra.models.SoldProduct
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
import com.example.eterra.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
        setUpUi()
    }

    private fun setUpUi() {
        binding.apply {
            tvOrderDetailsId.text = soldProduct.id

            val dateFormat = "dd MMM yyyy HH:mm"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = soldProduct.order_date
            tvOrderDetailsDate.text = formatter.format(calendar.time)

            try {
                Glide
                    .with(binding.root)
                    .load(soldProduct.image)
                    .centerCrop()
                    .into(binding.ivProductItemImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            tvProductItemName.text = soldProduct.title
            tvProductItemPrice.text = "$${soldProduct.price})"
            tvProductQuantity.text = "Quantity: ${soldProduct.sold_quantity}"

            tvSoldDetailsAddressType.text = soldProduct.address.type
            tvSoldDetailsFullName.text = soldProduct.address.name
            tvSoldDetailsAddress.text = soldProduct.address.address
            tvSoldDetailsAdditionalNote.text = soldProduct.address.additionalNote
            tvSoldDetailsOtherDetails.text = soldProduct.address.otherDetails
            tvSoldDetailsMobileNumber.text = soldProduct.address.mobileNumber

            tvSoldProductTotalAmount.text = "$${soldProduct.total_amount}"

        }
    }

}