package com.example.appquizlet

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.appquizlet.databinding.ActivityMainLoggedInBinding
import com.example.appquizlet.util.Helper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


class MainActivity_Logged_In : AppCompatActivity() {
    private lateinit var binding: ActivityMainLoggedInBinding

    //    private lateinit var userViewModel: UserViewModel
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = Helper.getDataUserId(this)
        Toast.makeText(this,userId,Toast.LENGTH_SHORT).show()


//        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
//        val data = userViewModel.getUserData()


//        userViewModel.getUserData().observe(this, Observer { userResponse ->
//            if (userResponse != null) {
//
//            }
//        })


        // khởi tạo đối tượng dialog
        // display all title and content in bottom nav
        binding.bottomNavigationView.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_LABELED

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

// Ẩn tiêu đề của mục "Add"
        bottomNavigationView.getOrCreateBadge(R.id.bottom_add).isVisible = false

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> replaceFragment(Home())
                R.id.bottom_solution -> replaceFragment(Solution())
                R.id.bottom_add -> showDialogBottomSheet()
                R.id.bottom_library -> replaceFragment(Library())
                R.id.bottom_edit_account -> replaceFragment(Profile())
                else -> {
                    replaceFragment(Home())
                }
            }
            true
        }

        // Check if it's the first time launching the app
        val prefs = getSharedPreferences("first", Context.MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("firstIn1", true)

        if (isFirstTime) {
            // It's the first time, replace the fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, Home())
                .commit()

            // Mark that the app has been launched
            prefs.edit().putBoolean("firstIn1", true).apply()
        }

    }

    private fun showDialogBottomSheet() {
        val addBottomSheet = Add()
        val transaction = supportFragmentManager.beginTransaction()
        addBottomSheet.show(transaction, Add.TAG)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

}