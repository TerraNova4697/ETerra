package com.example.eterra.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.eterra.R
import com.example.eterra.models.User
import com.example.eterra.ui.dashboard.DashboardFragmentDirections
import com.example.eterra.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignedInActivity : AppCompatActivity() {

    private lateinit var user: User
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.signed_in_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            user = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
            if (user.profileCompleted == 0) {
                val action = DashboardFragmentDirections.actionItemDashboardToUserProfileFragment(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    mobile = user.mobile,
                    gender = user.gender,
                    image = user.image
                )
                navController.navigate(action)
            }
        }

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}