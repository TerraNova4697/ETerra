package com.example.eterra.ui.userprofile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.eterra.R
import com.example.eterra.databinding.FragmentUserProfileBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.utils.GlideLoader
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class UserProfileFragment (): BaseFragment(R.layout.fragment_user_profile) {

    private lateinit var binding: FragmentUserProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserProfileBinding.bind(view)

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
                // TODO: Delegate to ViewModel
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        showImageChooser()
                    }
                    shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) -> {
                        Log.i(this@UserProfileFragment.javaClass.simpleName, "should show request permission rationale")
                        showErrorSnackBar("should show request permission rationale", false)
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }
                    else -> {
                        requestPermissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                        )
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showImageChooser()
            } else {
                showErrorSnackBar("Permission denied", true)
            }
        }

    private val pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                try {
                    val selectedImageUri = result.data!!.data
                    if (selectedImageUri != null) {
                        GlideLoader(requireContext()).loadUserPicture(selectedImageUri, binding.ivUserPhoto)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showErrorSnackBar(resources.getString(R.string.err_msg_image_selection_failed), true)
                }
            }
        }
    }

    // TODO: Make it reusable
    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickImageResultLauncher.launch(galleryIntent)
    }

}