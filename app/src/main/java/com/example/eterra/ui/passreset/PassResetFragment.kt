package com.example.eterra.ui.passreset

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.example.eterra.databinding.FragmentPassResetBinding
import com.example.eterra.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PassResetFragment: BaseFragment(R.layout.fragment_pass_reset) {

    private val passResetViewModel: PassResetViewModel by viewModels()
    private lateinit var binding: FragmentPassResetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPassResetBinding.bind(view)

        binding.apply {
            btnSubmit.setOnClickListener {
                passResetViewModel.onSubmitClicked(etEmail.text.toString())
            }
        }

        lifecycleScope.launchWhenCreated {
            passResetViewModel.passResetUiEvents.collect { event ->
                when (event) {
                    is PassResetViewModel.PassResetUiEvents.EnterEmailError -> {
                        showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    }
                    is PassResetViewModel.PassResetUiEvents.EmailSent -> {
                        showErrorSnackBar(resources.getString(R.string.msg_email_sent), false)
                        hideProgressBar()
                        findNavController().navigateUp()
                    }
                    is PassResetViewModel.PassResetUiEvents.ExceptionWhilePassReset -> {
                        showErrorSnackBar(event.msg, true)
                        hideProgressBar()
                    }
                    is PassResetViewModel.PassResetUiEvents.PassResetInProgress -> {
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