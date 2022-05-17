package com.example.eterra.ui.dashboard

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
import com.example.eterra.databinding.FragmentDashboardBinding
import com.example.eterra.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DashboardFragment(): BaseFragment(R.layout.fragment_dashboard) {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)

        lifecycleScope.launchWhenCreated {
            dashboardViewModel.dashboardViewModelEvents.collect { event ->
                when (event) {
                    is DashboardViewModel.DashboardUiEvent.ShowProgressBar -> {
                        showProgressBar(binding.progressBar)
                    }
                    is DashboardViewModel.DashboardUiEvent.HideProgressBar -> {
                        hideProgressBar(binding.progressBar)
                    }
                    is DashboardViewModel.DashboardUiEvent.ErrorFetchingProducts -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val action = DashboardFragmentDirections.actionItemDashboardToSettingsFragment()
                findNavController().navigate(action)
                true
            } else -> return super.onOptionsItemSelected(item)
        }

    }

}