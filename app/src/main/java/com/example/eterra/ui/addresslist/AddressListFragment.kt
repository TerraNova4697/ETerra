package com.example.eterra.ui.addresslist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentAddressListBinding
import com.example.eterra.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddressListFragment: BaseFragment(R.layout.fragment_address_list) {

    private lateinit var binding: FragmentAddressListBinding
    private val addressListViewModel: AddressListViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        addressListViewModel.collectAddresses()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddressListBinding.bind(view)

        binding.apply {
            tvAddAddress.setOnClickListener {
                val action = AddressListFragmentDirections.actionAddressListFragmentToAddEditAddressFragment()
                findNavController().navigate(action)
            }
        }

        addressListViewModel.addressList.observe(viewLifecycleOwner) {
            for (address in it) {
                Log.i(this@AddressListFragment.javaClass.simpleName, address.toString())
            }
        }

        lifecycleScope.launchWhenCreated {
            addressListViewModel.addressListUiEvents.collect { event ->
                when (event) {
                    is AddressListViewModel.AddressListUiEvent.ShowProgressbar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is AddressListViewModel.AddressListUiEvent.HideProgressbar -> {
                        hideProgressBar(binding.progressBar)
                    }
                    is AddressListViewModel.AddressListUiEvent.ShowError -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }
                }
            }
        }
    }

}