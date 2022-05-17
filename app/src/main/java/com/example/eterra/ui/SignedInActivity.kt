package com.example.eterra.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
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
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.signed_in_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        setupActionBarWithNavController(navController)


        // TODO: if profile not completed navigate to ProfileFragment
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            user = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
            if (user.profileCompleted == 0) {
                val action = DashboardFragmentDirections.actionItemDashboardToUserProfileFragment(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    mobile = user.mobile,
                    gender = user.gender,
                    image = user.image,
                    navFrom = this.javaClass.simpleName
                )
                navController.navigate(action)
            }
        }

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}