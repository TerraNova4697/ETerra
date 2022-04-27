package com.example.eterra.ui.login

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentLoginBinding
import com.example.eterra.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: BaseFragment(R.layout.fragment_login) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.hide()

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
        }
    }

}