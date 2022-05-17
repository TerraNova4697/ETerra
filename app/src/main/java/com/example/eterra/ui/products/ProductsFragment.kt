package com.example.eterra.ui.products

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentProductsBinding
import com.example.eterra.models.Product
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.adapters.ProductsListAdapter
import com.example.eterra.ui.dashboard.DashboardFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProductsFragment: BaseFragment(R.layout.fragment_products), ProductsListAdapter.AdapterClickListener {

    private val productsViewModel: ProductsViewModel by viewModels()
    private lateinit var binding: FragmentProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        productsViewModel.collectProducts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductsBinding.bind(view)

        val productsListAdapter = ProductsListAdapter(this)

        binding.apply {
            rvProductsList.apply {
                adapter = productsListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        productsViewModel.products.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.apply {
                    tvNoProductsYet.visibility = View.GONE
                    rvProductsList.visibility = View.VISIBLE
                }
                productsListAdapter.submitList(it)
            } else {
                binding.apply {
                    tvNoProductsYet.visibility = View.VISIBLE
                    rvProductsList.visibility = View.GONE
                }
            }
        }

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

    override fun onProductDeleteClickListener(productId: String) {
        Log.i(this@ProductsFragment.javaClass.simpleName, productId.toString())
        showAlertDialog(productId)
    }

    private fun showAlertDialog(productId: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_icon_alert, null))
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            productsViewModel.onProductDeleteClicked(productId)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}