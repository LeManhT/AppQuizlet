package com.example.appquizlet.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.appquizlet.R
import com.example.appquizlet.databinding.ActivityAdminActvityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityAdminActvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Get the NavHostFragment from the FragmentContainerView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentAdmin) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the BottomNavigationView with NavController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}