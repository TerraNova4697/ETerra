package com.example.eterra.ui.settings

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.eterra.R
import com.example.eterra.databinding.FragmentSettingsBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
import com.example.eterra.utils.GlideLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SettingsFragment(): BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.hide()

        (activity as SignedInActivity).bottomNavigationView.visibility = View.GONE

        lifecycleScope.launchWhenCreated {
            settingsViewModel.settingsUiEvent.collect { event ->
                when (event) {
                    is SettingsViewModel.SettingsUiEvent.UserData -> {
                        binding.apply {
                            tvName.text = event.name
                            tvGender.text = event.gender
                            tvEmail.text = event.email
                            tvMobileNumber.text = event.mobileNumber
                            GlideLoader(requireContext()).loadUserPicture(Uri.parse(event.imageUrl), ivUserPhoto)
                            hideProgressBar()
                        }
                    }
                }
            }
        }

        getUserDetails()
    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as AppCompatActivity).supportActionBar?.show()

        (activity as SignedInActivity).bottomNavigationView.visibility = View.VISIBLE
    }

    private fun getUserDetails() {
        showProgressBar()
        settingsViewModel.onGetUserDetails()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}