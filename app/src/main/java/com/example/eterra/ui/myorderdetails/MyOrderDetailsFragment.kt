package com.example.eterra.ui.myorderdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eterra.R
import com.example.eterra.models.Order
import com.example.eterra.ui.BaseFragment
import com.example.eterra.utils.Constants

class MyOrderDetailsFragment: BaseFragment(R.layout.fragment_my_order_details) {

    private lateinit var order: Order

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        order = arguments?.getParcelable(Constants.EXTRA_MY_ORDER_DETAILS) ?: Order()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}