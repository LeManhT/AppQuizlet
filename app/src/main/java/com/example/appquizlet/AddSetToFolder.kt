package com.example.appquizlet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.appquizlet.adapter.ViewPagerTabAddSet
import com.example.appquizlet.databinding.ActivityAddSetToFolderBinding
import com.google.android.material.tabs.TabLayoutMediator

class AddSetToFolder : AppCompatActivity() {
    private lateinit var binding: ActivityAddSetToFolderBinding
    private lateinit var folderId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSetToFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại

        val intent = intent
        folderId = intent.getStringExtra("folderAddSet").toString()

        val adapterTabSet = ViewPagerTabAddSet(supportFragmentManager, lifecycle)
        binding.pagerAddSetLib.adapter = adapterTabSet
        TabLayoutMediator(binding.tabLibAddSet, binding.pagerAddSetLib) { tab, pos ->
            when (pos) {
                0 -> tab.text = resources.getString(R.string.created)
                1 -> tab.text = resources.getString(R.string.studied)
                2 -> tab.text = resources.getString(R.string.folders)
            }
        }.attach()

        binding.iconAddToSet.setOnClickListener {
            // Lấy fragment hiện tại của ViewPager
            val currentFragment =
                supportFragmentManager.fragments[binding.pagerAddSetLib.currentItem]

            if (currentFragment is FragmentCreatedSet) {
                // Gọi phương thức trong fragment
                currentFragment.insertSetToFolder(folderId)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}