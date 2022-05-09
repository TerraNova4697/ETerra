package com.example.eterra.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.eterra.R
import com.example.eterra.models.User
import com.example.eterra.ui.main.MainFragmentDirections
import com.example.eterra.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignedInActivity : AppCompatActivity() {

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.signed_in_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            user = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
        }

        if (user != null && user.profileCompleted == 0) {
            val action = MainFragmentDirections.actionMainFragmentToUserProfileFragment(
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                mobile = user.mobile,
                gender = user.gender
            )
            navController.navigate(action)
        } else if (user == null) {
            throw Exception("User does not exist")
        }
    }
}