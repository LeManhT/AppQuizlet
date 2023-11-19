package com.example.appquizlet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.appquizlet.databinding.ActivityMainLoggedInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


class MainActivity_Logged_In : AppCompatActivity() {
    private lateinit var binding: ActivityMainLoggedInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

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