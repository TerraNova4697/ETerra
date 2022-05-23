package com.example.eterra.ui.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eterra.R
import com.example.eterra.databinding.FragmentOrdersBinding
import com.example.eterra.models.Order
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.adapters.OrdersListAdapter
import com.example.eterra.ui.adapters.ProductsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OrdersFragment(): BaseFragment(R.layout.fragment_orders), OrdersListAdapter.AdapterClickListener {

    private lateinit var binding: FragmentOrdersBinding
    private val ordersViewModel: OrdersViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        ordersViewModel.getOrdersList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrdersBinding.bind(view)

        val ordersListAdapter = OrdersListAdapter(this)

        binding.apply {
            rvOrdersItems.apply {
                adapter = ordersListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        ordersViewModel.orders.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.apply {
                    tvNoOrdersYet.visibility = View.GONE
                    rvOrdersItems.visibility = View.VISIBLE
                }
                ordersListAdapter.submitList(it)
            } else {
                binding.apply {
                    tvNoOrdersYet.visibility = View.VISIBLE
                    rvOrdersItems.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            ordersViewModel.ordersUiEvent.collect { event ->
                when (event) {
                    is OrdersViewModel.OrdersUiEvent.ShowProgressbar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is OrdersViewModel.OrdersUiEvent.HideProgressbar -> {
                        hideProgressBar(binding.progressBar)
                    }
                    is OrdersViewModel.OrdersUiEvent.NotifyError -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }

                }
            }
        }
    }

    override fun onProductDeleteClickListener(productId: String) {
        TODO("Not yet implemented")
    }

    override fun onProductClicked(order: Order) {
        val action = OrdersFragmentDirections.actionItemOrdersToMyOrderDetailsFragment(order)
        findNavController().navigate(action)
    }

}