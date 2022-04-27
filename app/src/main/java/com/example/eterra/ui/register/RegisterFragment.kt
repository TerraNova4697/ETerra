package com.example.eterra.ui.register

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            tvLogin.setOnClickListener { requireActivity().onBackPressed() }
            btnRegister.setOnClickListener {
                viewModel.onBtnRegisterClicked(
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    etPasswordConfirm.text.toString()
                )
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is RegisterViewModel.UiEvent.EnterEmailError -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    }
                    is RegisterViewModel.UiEvent.EnterPassword -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_pass), true)
                    }
                    is RegisterViewModel.UiEvent.PasswordNotMatch -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_pass_incorrect), true)
                    }
                    is RegisterViewModel.UiEvent.ValidData -> {
                        showErrorSnackBar("Data is valid", false)
                    }
                    is RegisterViewModel.UiEvent.ErrorWhileRegistering -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_try_again), true)
                    }
                    is RegisterViewModel.UiEvent.SignInUser -> {
                        showErrorSnackBar("Registered", false)
                    }
                }
            }
        }
    }

}