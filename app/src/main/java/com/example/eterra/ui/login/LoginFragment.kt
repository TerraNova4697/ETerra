package com.example.eterra.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentLoginBinding
import com.example.eterra.ui.BaseFragment
import com.example.eterra.ui.SignedInActivity
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
            btnLogin.setOnClickListener {
                loginViewModel.onBtnLoginClicked(etEmail.text.toString(), etPassword.text.toString())
            }
        }

        // TODO: Add event manager
        lifecycleScope.launchWhenCreated {
            loginViewModel.loginUiEvents.collect { event ->
                when (event) {
                    is LoginViewModel.LoginUiEvent.EnterEmailError -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    }
                    is LoginViewModel.LoginUiEvent.EnterPassword -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_pass), true)
                    }
                    is LoginViewModel.LoginUiEvent.SignInSuccess -> {
                        hideProgressBar()
                        val intent = Intent(requireContext(), SignedInActivity::class.java)
                        intent.putExtra("user_id", event.userId)
                        intent.putExtra("email", event.email)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is LoginViewModel.LoginUiEvent.SignInFailed -> {
                        hideProgressBar()
                        showErrorSnackBar(event.exception, true)
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