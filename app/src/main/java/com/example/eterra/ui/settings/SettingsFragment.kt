package com.example.eterra.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentSettingsBinding
import com.example.eterra.models.User
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
import com.example.eterra.ui.login.MainActivity
import com.example.eterra.utils.GlideLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SettingsFragment(): BaseFragment(R.layout.fragment_settings) {

    private lateinit var user: User
    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.hide()


        binding.apply {
            btnLogout.setOnClickListener {
                settingsViewModel.onLogoutClicked()
            }
            tvEdit.setOnClickListener {
                val action = SettingsFragmentDirections.actionSettingsFragmentToUserProfileFragment(
                    firstName = user.firstName,
                    email = user.email,
                    mobile = user.mobile,
                    gender = user.gender.lowercase(),
                    lastName = user.lastName,
                    image = user.image,
                    navFrom = this@SettingsFragment.javaClass.simpleName
                )
                findNavController().navigate(action)
//                settingsViewModel.onEditClicked()
            }
        }

        lifecycleScope.launchWhenCreated {
            settingsViewModel.settingsUiEvent.collect { event ->
                when (event) {
                    is SettingsViewModel.SettingsUiEvent.UserData -> {
                        binding.apply {
                            user = User(
                                firstName = event.firstName,
                                lastName = event.lastName,
                                gender = event.gender,
                                email = event.email,
                                mobile = event.mobileNumber.toLong(),
                                image = event.imageUrl
                            )
                            val name = "${event.firstName} ${event.lastName}"
                            tvName.text = name
                            tvGender.text = event.gender
                            tvEmail.text = event.email
                            tvMobileNumber.text = event.mobileNumber
                            GlideLoader(requireContext()).loadUserPicture(Uri.parse(event.imageUrl), ivUserPhoto)
                            hideProgressBar()
                        }
                    }
                    is SettingsViewModel.SettingsUiEvent.UserLoggedOut -> {
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is SettingsViewModel.SettingsUiEvent.NavigateToProfileFragment -> {

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