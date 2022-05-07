package com.example.eterra.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentRegisterBinding
import com.example.eterra.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RegisterFragment: BaseFragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            tvLogin.setOnClickListener { requireActivity().onBackPressed() }
            btnRegister.setOnClickListener {
                viewModel.onBtnRegisterClicked(
                    etFName.text.toString(),
                    etLName.text.toString(),
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    etPasswordConfirm.text.toString()
                )
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is RegisterViewModel.RegisterUiEvent.EnterFirstNameError -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                    }
                    is RegisterViewModel.RegisterUiEvent.EnterLastNameError -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                    }
                    is RegisterViewModel.RegisterUiEvent.EnterEmailError -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    }
                    is RegisterViewModel.RegisterUiEvent.EnterPassword -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_pass), true)
                    }
                    is RegisterViewModel.RegisterUiEvent.PasswordNotMatch -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_pass_incorrect), true)
                    }
                    is RegisterViewModel.RegisterUiEvent.ValidData -> {
                        showErrorSnackBar("Data is valid", false)
                    }
                    is RegisterViewModel.RegisterUiEvent.ErrorWhileRegistering -> {
                        hideProgressBar()
                        showErrorSnackBar(event.message, true)
                    }
                    is RegisterViewModel.RegisterUiEvent.SignInUser -> {
                        hideProgressBar()
                        showErrorSnackBar("Registered successfully", false)
                        findNavController().navigateUp()
                    }
                    is RegisterViewModel.RegisterUiEvent.RegisteringInProgress -> {
                        showProgressBar()
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