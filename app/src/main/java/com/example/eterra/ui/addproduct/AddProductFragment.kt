package com.example.eterra.ui.addproduct

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.eterra.R
import com.example.eterra.databinding.FragmentAddProductBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.utils.GlideLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.IOException

@AndroidEntryPoint
class AddProductFragment: BaseFragment(R.layout.fragment_add_product) {

    private val addProductViewModel: AddProductViewModel by viewModels()
    private lateinit var selectedImageUri: Uri
    private lateinit var binding: FragmentAddProductBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddProductBinding.bind(view)

        binding.apply {
            ivAddPhoto.setOnClickListener {
                addProductViewModel.onAddPhotoClicked()
            }
        }

        lifecycleScope.launchWhenCreated {
            addProductViewModel.addProductUiEvents.collect { event ->
                when (event) {
                    is AddProductViewModel.AddProductUiEvent.PickPhoto -> {
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

    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickImageResultLauncher.launch(galleryIntent)
    }

    private val pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                try {
                    selectedImageUri = result.data!!.data!!
                    binding.ivAddPhoto.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_edit_24))
                    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(requireActivity().contentResolver.getType(selectedImageUri))
                    GlideLoader(requireContext()).loadUserPicture(selectedImageUri, binding.ivProductImage)
//                    userProfileViewModel.onImagePicked(selectedImageUri, extension ?: "")
                } catch (e: IOException) {
                    e.printStackTrace()
                    showErrorSnackBar(resources.getString(R.string.err_msg_image_selection_failed), true)
                }
            }
        }
    }

}