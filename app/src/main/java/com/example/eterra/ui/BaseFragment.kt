package com.example.eterra.ui

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.eterra.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class BaseFragment(layout: Int): Fragment(layout) {



    fun showErrorSnackBar(msg: String, errorMessage: Boolean) {
        val snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)

        if (errorMessage) {
            snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSnackbarError))
        } else {
            snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSnackbarSuccess))
        }

        snackbar.show()
    }



}