package com.example.eterra.ui.cartlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.eterra.R
import com.example.eterra.databinding.FragmentCartListBinding
import com.example.eterra.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartListFragment: BaseFragment(R.layout.fragment_cart_list) {

    private lateinit var binding: FragmentCartListBinding
    private val cartListViewModel: CartListViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        cartListViewModel.collectCartItems()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartListBinding.bind(view)
    }

}