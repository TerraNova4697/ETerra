package com.example.eterra.ui.products

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentProductsBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.dashboard.DashboardFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProductsFragment: BaseFragment(R.layout.fragment_products) {

    private val productsViewModel: ProductsViewModel by viewModels()
    private lateinit var binding: FragmentProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductsBinding.bind(view)
//        productsViewModel.collectProducts()
//        lifecycleScope.launchWhenCreated {
//            productsViewModel.productsViewModelEvents.collect { event ->
//                when (event) {
//                    is
//                }
//            }
//        }

        lifecycleScope.launchWhenCreated {
            productsViewModel.productsViewModelEvents.collect { event ->
                when (event) {
                    is ProductsViewModel.ProductsUiEvent.ErrorFetchingProducts -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }
                    is ProductsViewModel.ProductsUiEvent.ShowProgressBar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is ProductsViewModel.ProductsUiEvent.HideProgressBar -> {
                        hideProgressBar(binding.progressBar)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.products_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_product -> {
                val action = ProductsFragmentDirections.actionItemProductsToAddProductFragment()
                findNavController().navigate(action)
                true
            } else -> return super.onOptionsItemSelected(item)
        }
    }
}