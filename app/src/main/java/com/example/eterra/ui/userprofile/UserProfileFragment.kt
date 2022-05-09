package com.example.eterra.ui.userprofile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eterra.R
import com.example.eterra.databinding.FragmentUserProfileBinding
import com.example.eterra.ui.BaseFragment

class UserProfileFragment : BaseFragment(R.layout.fragment_user_profile) {


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showErrorSnackBar("Permission granted", false)
            } else {
                showErrorSnackBar("Permission denied", true)
            }
        }


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

            ivUserPhoto.setOnClickListener {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        showErrorSnackBar("Permission granted", false)
                    }
                    shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) -> {
                        Log.i(this@UserProfileFragment.javaClass.simpleName, "should show request permission rationale")
                        showErrorSnackBar("should show request permission rationale", false)
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    else -> {
                        Log.i(this@UserProfileFragment.javaClass.simpleName, "3 option")
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestPermissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE,

                        )
                    }
                }
            }
        }
    }

}