package com.example.eterra.ui.userprofile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eterra.R
import com.example.eterra.databinding.FragmentUserProfileBinding
import com.example.eterra.ui.BaseFragment

class UserProfileFragment: BaseFragment(R.layout.fragment_user_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUserProfileBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.apply {
            etFirstName.isEnabled = false
            etLastName.isEnabled = false
            etEmail.isEnabled = false
            etFirstName.setText(arguments?.getString("firstName"))
            etLastName.setText(arguments?.getString("lastName"))
            etEmail.setText(arguments?.getString("email"))
            etMobileNumber.setText(arguments?.getLong("mobile").toString())
            rbFemale.isChecked = arguments?.getString("gender") == "female"
        }
    }

}