package com.example.eterra.ui.addeditaddress

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentAddEditFragmentBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditAddressFragment: BaseFragment(R.layout.fragment_add_edit_fragment) {

    private val addEditAddressViewModel: AddEditAddressViewModel by viewModels()
    private lateinit var binding: FragmentAddEditFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEditFragmentBinding.bind(view)

        binding.apply {
            btnSubmitAddress.setOnClickListener {
                var type = Constants.HOME
                if (rbOffice.isChecked) {
                    type = Constants.OFFICE
                } else if (rbOther.isChecked) {
                    type = Constants.OTHER
                }
                addEditAddressViewModel.onBtnSubmitClicked(
                    etFullName.text.toString(),
                    etPhoneNumber.text.toString(),
                    etAddress.text.toString(),
                    etZipCode.text.toString(),
                    type,
                    etAdditionalNote.text.toString()
                )
            }
        }

        lifecycleScope.launchWhenCreated {
            addEditAddressViewModel.addEditAddressUiEvents.collect { event ->
                when (event) {
                    is AddEditAddressViewModel.AddEditAddressUiEvent.NameErrorEvent -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_full_name), true)
                        hideProgressBar()
                    }
                    is AddEditAddressViewModel.AddEditAddressUiEvent.NumberErrorEvent -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                        hideProgressBar()
                    }
                    is AddEditAddressViewModel.AddEditAddressUiEvent.AddressErrorEvent -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_address), true)
                        hideProgressBar()
                    }
                    is AddEditAddressViewModel.AddEditAddressUiEvent.ZipCodeErrorEvent -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_zip_code), true)
                        hideProgressBar()
                    }
                    is AddEditAddressViewModel.AddEditAddressUiEvent.ShowProgressBar -> {
                        showProgressBar()
                    }
                    is AddEditAddressViewModel.AddEditAddressUiEvent.HideProgressBar -> {
                        hideProgressBar()
                    }
                    is AddEditAddressViewModel.AddEditAddressUiEvent.NavigateUp -> {
                        findNavController().navigateUp()
                    }
                    is AddEditAddressViewModel.AddEditAddressUiEvent.ShowErrorSnackBar -> {
                        showErrorSnackBar(event.errorMessage, true)
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}