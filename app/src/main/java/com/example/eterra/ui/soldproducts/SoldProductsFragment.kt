package com.example.eterra.ui.soldproducts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentSoldProductsBinding
import com.example.eterra.models.SoldProduct
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.adapters.SoldProductsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SoldProductsFragment: BaseFragment(R.layout.fragment_sold_products),  SoldProductsListAdapter.AdapterClickListener{

    private lateinit var binding: FragmentSoldProductsBinding
    private val soldProductsViewModel: SoldProductsViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        soldProductsViewModel.getSoldProductsList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSoldProductsBinding.bind(view)

        val soldProductsListAdapter = SoldProductsListAdapter(this)

        binding.apply {
            rvSoldProducts.apply {
                adapter = soldProductsListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        soldProductsViewModel.soldProducts.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.apply {
                    tvNoProductsYet.visibility = View.GONE
                    rvSoldProducts.visibility = View.VISIBLE
                }
                soldProductsListAdapter.submitList(it)
            } else {
                binding.apply {
                    tvNoProductsYet.visibility = View.VISIBLE
                    rvSoldProducts.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            soldProductsViewModel.soldProductsUiEvent.collect { event ->
                when (event) {
                    is SoldProductsViewModel.SoldProductsUiEvent.ShowProgressbar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is SoldProductsViewModel.SoldProductsUiEvent.HideProgressbar -> {
                        hideProgressBar(binding.progressBar)
                    }
                    is SoldProductsViewModel.SoldProductsUiEvent.NotifyError -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }

                }
            }
        }
    }

    override fun onProductClicked(soldProduct: SoldProduct) {
        val action = SoldProductsFragmentDirections.actionItemSoldProductsToSoldProductDetailsFragment(soldProduct)
        findNavController().navigate(action)
    }

}