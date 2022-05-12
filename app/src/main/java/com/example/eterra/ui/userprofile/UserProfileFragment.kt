package com.example.eterra.ui.userprofile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentUserProfileBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
import com.example.eterra.ui.settings.SettingsFragment
import com.example.eterra.utils.Constants
import com.example.eterra.utils.GlideLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.IOException

@AndroidEntryPoint
class UserProfileFragment : BaseFragment(R.layout.fragment_user_profile) {

    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var selectedImageUri: Uri
    private lateinit var navFrom: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserProfileBinding.bind(view)
        navFrom = arguments?.getString("navFrom") ?: Constants.SETTINGS_FRAGMENT
        Log.i(this.javaClass.simpleName, navFrom)

        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as SignedInActivity).bottomNavigationView.visibility = View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            onBackPressedCallback
        )

        binding.apply {
            etFirstName.isEnabled = false
            etLastName.isEnabled = false
            etEmail.isEnabled = false
            etFirstName.setText(arguments?.getString("firstName"))
            etLastName.setText(arguments?.getString("lastName"))
            etEmail.setText(arguments?.getString("email"))
            val mobileNumber = arguments?.getLong("mobile").toString()
            etMobileNumber.setText(if (mobileNumber.length > 1) mobileNumber else "")
            rbFemale.isChecked = arguments?.getString("gender") == "female"
            if (arguments?.getString("image")!! != "") {
                GlideLoader(requireContext()).loadUserPicture(
                    Uri.parse(arguments?.getString("image")),
                    binding.ivUserPhoto
                )
            }

            btnSubmit.setOnClickListener {
                userProfileViewModel.onBtnSubmitClicked(
                    mobileNumber = binding.etMobileNumber.text.toString(),
                    gender = if (binding.rbMale.isChecked) Constants.MALE else Constants.FEMALE
                )
            }

            ivUserPhoto.setOnClickListener {
                userProfileViewModel.onIvUserPhotoClicked()
            }
        }
        lifecycleScope.launchWhenCreated {
            userProfileViewModel.userProfileEvents.collect { event ->
                when (event) {
                    is UserProfileViewModel.UserProfileEvents.ShowProgressDialog -> {
                        showProgressBar()
                    }
                    is UserProfileViewModel.UserProfileEvents.EnterMobileNumberError -> {
                        showErrorSnackBar(
                            resources.getString(R.string.err_msg_enter_mobile_number),
                            true
                        )
                    }
                    is UserProfileViewModel.UserProfileEvents.ProfileCompleted -> {

                        showErrorSnackBar("Profile completed", false)
                    }
                    is UserProfileViewModel.UserProfileEvents.PickImage -> {
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
                                showErrorSnackBar("The app needs permission to pick photo", false)
                                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                            else -> {
                                requestPermissionLauncher.launch(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                )
                            }
                        }
                    }
                    is UserProfileViewModel.UserProfileEvents.NavigateBack -> {
                        hideProgressBar()
                        findNavController().navigateUp()
                    }
                    is UserProfileViewModel.UserProfileEvents.ErrorWhileUpdating -> {
                        hideProgressBar()
                        showErrorSnackBar(event.message, true)
                    }
                    is UserProfileViewModel.UserProfileEvents.PlaceImage -> {
//                        selectedImageUri = event.imageUri
                        GlideLoader(requireContext()).loadUserPicture(
                            event.imageUri,
                            binding.ivUserPhoto
                        )
                    }
                    is UserProfileViewModel.UserProfileEvents.HideProgressDialog -> {
                        hideProgressBar()
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

    private val pickImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    try {
                        selectedImageUri = result.data!!.data!!
                        if (selectedImageUri != null) {
                            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                                requireActivity().contentResolver.getType(selectedImageUri)
                            )
//                        GlideLoader(requireContext()).loadUserPicture(selectedImageUri, binding.ivUserPhoto)
                            userProfileViewModel.onImagePicked(selectedImageUri, extension ?: "")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showErrorSnackBar(
                            resources.getString(R.string.err_msg_image_selection_failed),
                            true
                        )
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (navFrom == Constants.SETTINGS_FRAGMENT) {
                findNavController().navigateUp()
            }
        }
    }

}