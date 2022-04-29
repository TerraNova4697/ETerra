package com.example.eterra.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eterra.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignedInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)
    }
}