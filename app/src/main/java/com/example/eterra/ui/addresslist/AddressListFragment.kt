package com.example.eterra.ui.addresslist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentAddressListBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.adapters.AddressListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddressListFragment: BaseFragment(R.layout.fragment_address_list), AddressListAdapter.OnItemClickedListener {

    private lateinit var binding: FragmentAddressListBinding
    private val addressListViewModel: AddressListViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        addressListViewModel.collectAddresses()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddressListBinding.bind(view)

        val addressListAdapter = AddressListAdapter(this)

        binding.apply {
            tvAddAddress.setOnClickListener {
                val action = AddressListFragmentDirections.actionAddressListFragmentToAddEditAddressFragment()
                findNavController().navigate(action)
            }
            rvAddressList.apply {
                adapter = addressListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        addressListViewModel.addressList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvNoAddressFound.visibility = View.VISIBLE
                binding.rvAddressList.visibility = View.GONE
            } else {
                binding.tvNoAddressFound.visibility = View.GONE
                binding.rvAddressList.visibility = View.VISIBLE
                addressListAdapter.submitList(it)
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