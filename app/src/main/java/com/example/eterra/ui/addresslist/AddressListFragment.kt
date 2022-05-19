package com.example.eterra.ui.addresslist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentAddressListBinding
import com.example.eterra.ui.BaseFragment

class AddressListFragment: BaseFragment(R.layout.fragment_address_list) {

    private lateinit var binding: FragmentAddressListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddressListBinding.bind(view)

        binding.apply {
            tvAddAddress.setOnClickListener {
                val action = AddressListFragmentDirections.actionAddressListFragmentToAddEditAddressFragment()
                findNavController().navigate(action)
            }
        }
    }

}