package com.example.appquizlet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.appquizlet.databinding.ActivityMainLoggedInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationBarView

private lateinit var binding: ActivityMainLoggedInBinding

class MainActivity_Logged_In : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.appquizlet.binding = ActivityMainLoggedInBinding.inflate(layoutInflater)
        setContentView(com.example.appquizlet.binding.root)

        // khởi tạo đối tượng dialog
        // display all title and content in bottom nav
        com.example.appquizlet.binding.bottomNavigationView.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_LABELED

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

// Ẩn tiêu đề của mục "Add"
        bottomNavigationView.getOrCreateBadge(R.id.bottom_add).isVisible = false

        com.example.appquizlet.binding.bottomNavigationView.setOnItemSelectedListener { it ->
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
//        val build = AlertDialog.Builder(this,R.style.DialogAnimation)
//        val dialog = Dialog(this)
//        val dialogBinding = LayoutAddBottomsheetBinding.inflate(LayoutInflater.from(this))
        // create the overlay view
//        val overlayView = LayoutInflater.from(this).inflate(R.layout.layout_overlay, null)

//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.layout_add_bottomsheet)
//        build.setView(dialogBinding.root)
//        dialog = build.create()
        val addBottomSheet = Add()
        val transaction = supportFragmentManager.beginTransaction()
        addBottomSheet.show(transaction, Add.TAG)


//        dialog.show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
//        dialog.window?.setGravity(Gravity.BOTTOM)
//
//        dialog.setOnDismissListener { dialog.dismiss() }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}