package com.example.eterra.ui.myorderdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentMyOrderDetailsBinding
import com.example.eterra.models.Order
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
import com.example.eterra.ui.adapters.CartItemsListAdapter
import com.example.eterra.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class MyOrderDetailsFragment: BaseFragment(R.layout.fragment_my_order_details), CartItemsListAdapter.CartItemsClickListeners {

    private var order: Order = Order()
    private lateinit var binding: FragmentMyOrderDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        order = arguments?.getParcelable(Constants.EXTRA_MY_ORDER_DETAILS) ?: Order()
        (requireActivity() as SignedInActivity).supportActionBar?.hide()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyOrderDetailsBinding.bind(view)
        val cartItemsListAdapter = CartItemsListAdapter(this@MyOrderDetailsFragment, false)
        setUpUi()
        binding.rvMyOrderItemsList.adapter = cartItemsListAdapter
        cartItemsListAdapter.submitList(order.items)
    }

    private fun setUpUi() {
        binding.apply {
            tvOrderDetailsId.text = order.id

            val dateFormat = "dd MMM yyyy HH:mm"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = order.order_datetime
            val orderDateTime = formatter.format(calendar.time)
            tvOrderDetailsDate.text = orderDateTime

            tvOrderStatus.text = order.status
            when (order.status){
                Constants.DELIVERY_STATUS_IN_PROCESS -> {
                    tvOrderStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_order_status_in_process))
                }
                Constants.DELIVERY_STATUS_PENDING -> {
                    tvOrderStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_order_status_pending))
                }
                Constants.DELIVERY_STATUS_DELIVERED -> {
                    tvOrderStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_order_status_delivered))
                }
            }

            rvMyOrderItemsList.layoutManager = LinearLayoutManager(requireContext())
            rvMyOrderItemsList.setHasFixedSize(true)

            tvMyOrderDetailsAddressType.text = order.address.type
            tvMyOrderDetailsFullName.text = order.address.name
            tvMyOrderDetailsAddress.text = order.address.address
            tvMyOrderDetailsAdditionalNote.text = order.address.additionalNote
            tvMyOrderDetailsOtherDetails.text = order.address.otherDetails
            tvMyOrderDetailsMobileNumber.text = order.address.mobileNumber

            tvOrderDetailsSubTotal.text = order.sub_total_amount
            tvOrderDetailsShippingCharge.text = order.shipping_charge
            tvOrderDetailsTotalAmount.text = order.total_amount


        }
    }

    override fun onDeleteClicked(cartId: String) {
        TODO("Not yet implemented")
    }

    override fun addOneItem(cartId: String, currentCartQuantity: String, stockQuantity: String) {
        TODO("Not yet implemented")
    }

    override fun removeOneItem(cartId: String, currentCartQuantity: String) {
        TODO("Not yet implemented")
    }

}