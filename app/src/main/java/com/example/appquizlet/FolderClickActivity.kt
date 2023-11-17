package com.example.appquizlet

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.databinding.ActivityFolderClickBinding
import com.example.appquizlet.model.StudySetItemData

class FolderClickActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFolderClickBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderClickBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        set toolbar back display
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại
// Tắt tiêu đề của Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val listStudySet = mutableListOf<StudySetItemData>()
        listStudySet.add(StudySetItemData("Everyday word 1", 3, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 2", 15, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 3", 5, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))

        val adapterStudySet = RvStudySetItemAdapter(listStudySet)
        val rvStudySet = binding.rvStudySet
        rvStudySet.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvStudySet.adapter = adapterStudySet

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.option_edit -> {
                showEditCustomDialog(
                    resources.getString(R.string.edit_folder),
                    resources.getString(R.string.folder_name),
                    resources.getString(R.string.desc_optional)
                )
                return true
            }

            R.id.option_delete -> {
                showDeleteDialog(
                    resources.getString(R.string.delete_text),
                )
            }

            R.id.option_share -> {
                shareDialog("Share content")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    private  fun showEditCustomDialog(title: String, folderNameHint: String, folderDescHint: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60)

        val editTextFolderName = EditText(this)
        editTextFolderName.hint = folderNameHint
        layout.addView(editTextFolderName)


        val editTextFolderDesc = EditText(this)
        editTextFolderDesc.hint = folderDescHint
        layout.addView(editTextFolderDesc)

        // Set layout parameters with margins
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 10
        layoutParams.bottomMargin = 10
        editTextFolderName.layoutParams = layoutParams
        editTextFolderDesc.layoutParams = layoutParams

        builder.setView(layout)

        builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
            Toast.makeText(this, "Click Ok", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()

    }

    private fun showDeleteDialog(desc : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(resources.getString(R.string.delete)) {
            dialog , _ ->       Toast.makeText(this, "Click delete", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) {
            dialog,_ -> dialog.dismiss()
        }
        builder.create().show()
    }

    private fun shareDialog(content: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content)

        val packageNames = arrayOf("com.facebook.katana", "com.facebook.orca", "com.google.android.gm")
        val chooserIntent = Intent.createChooser(sharingIntent, "Share via")
        chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, packageNames)

        startActivity(sharingIntent)
    }
}