package com.example.eterra.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.eterra.R
import com.example.eterra.databinding.FragmentSettingsBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment(): BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.hide()

        (activity as SignedInActivity).bottomNavigationView.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as AppCompatActivity).supportActionBar?.show()

        (activity as SignedInActivity).bottomNavigationView.visibility = View.VISIBLE
    }

    private fun getUserDetails() {
        showProgressBar()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}