package com.example.eterra.ui.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentLoginBinding
import com.example.eterra.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment: BaseFragment(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.hide()

        // TODO: Delegate btn setOnClickListeners to viewModel
        binding.apply {
            tvRegister.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }
            tvForgotPass.setOnClickListener {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToPassResetFragment()
                )
            }
            tvLogin.setOnClickListener {
                loginViewModel.onBtnLoginClicked(etEmail.text.toString(), etPassword.text.toString())
            }
        }

        lifecycleScope.launchWhenCreated {
            loginViewModel.loginUiEvents.collect { event ->
                when (event) {
                    is LoginViewModel.LoginUiEvent.EnterEmailError -> {

                    }
                    is LoginViewModel.LoginUiEvent.EnterPassword -> {

                    }
                    is LoginViewModel.LoginUiEvent.SignInSuccess -> {
                        hideProgressBar()

                    }
                    is LoginViewModel.LoginUiEvent.SignInFailed -> {
                        hideProgressBar()

                    }
                    is LoginViewModel.LoginUiEvent.SigningInInProgress -> {
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