package com.example.appquizlet

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityFolderClickBinding
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.StudySetItemData
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class FolderClickActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding: ActivityFolderClickBinding
    private lateinit var apiService: ApiService
    private lateinit var folderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderClickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        folderId = intent.getStringExtra("idFolder").toString()

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

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

        val adapterStudySet = RvStudySetItemAdapter(listStudySet, object : RVStudySetItem {
            override fun handleClickStudySetItem(setItem: StudySetItemData) {
                val i = Intent(this@FolderClickActivity, StudySetDetail::class.java)
                startActivity(i)
            }
        })


//        val userData = UserM.getUserData()
//        userData.observe(this, Observer { userResponse ->
//            listStudySet.clear()
//            listStudySet.addAll(userResponse.documents.studySets)
//            adapterStudySet.notifyDataSetChanged()
//        })
//        val rvStudySet = binding.rvStudySet
//        rvStudySet.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        rvStudySet.adapter = adapterStudySet

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

            R.id.option_add_sets -> {
                val i = Intent(this, AddSetToFolder::class.java)
                startActivity(i)
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

    private fun showEditCustomDialog(
        title: String,
        folderNameHint: String,
        folderDescHint: String,
    ) {
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
            updateFolder(
                editTextFolderName.text.toString(),
                editTextFolderDesc.text.toString(),
                Helper.getDataUserId(this),
                folderId
            )
            dialog.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()

    }

    private fun showDeleteDialog(desc: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
            deleteFolder(Helper.getDataUserId(this), folderId)
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun shareDialog(content: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content)

        val packageNames =
            arrayOf("com.facebook.katana", "com.facebook.orca", "com.google.android.gm")
        val chooserIntent = Intent.createChooser(sharingIntent, "Share via")
        chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, packageNames)

        startActivity(sharingIntent)
    }

    private fun updateFolder(name: String, desc: String? = "", userId: String, folderId: String) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.updateLoadMes))
            try {
                val body = JsonObject().apply {
                    addProperty(resources.getString(R.string.createFolderNameField), name)
                    addProperty(resources.getString(R.string.descriptionField), desc)
                }
                val result = apiService.updateFolder(userId, folderId, body)
                if (result.isSuccessful) {
                    result.body().let { it ->
                        if (it != null) {
                            this@FolderClickActivity.let { it1 ->
                                CustomToast(it1).makeText(
                                    this@FolderClickActivity,
                                    resources.getString(R.string.create_folder_success),
                                    CustomToast.LONG,
                                    CustomToast.SUCCESS
                                ).show()
                                UserM.setUserData(it)
                            }
                        }
                    }
                } else {
                    result.errorBody()?.string()?.let {
                        this@FolderClickActivity.let { it1 ->
                            CustomToast(it1).makeText(
                                this@FolderClickActivity,
                                it,
                                CustomToast.LONG,
                                CustomToast.ERROR
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                CustomToast(this@FolderClickActivity).makeText(
                    this@FolderClickActivity,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                )
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun deleteFolder(userId: String, folderId: String) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.deleteFolderLoading))
            try {
                val result = apiService.deleteFolder(userId, folderId)
                if (result.isSuccessful) {

                    result.body().let {
                        if (it != null) {
                            CustomToast(this@FolderClickActivity).makeText(
                                this@FolderClickActivity,
                                resources.getString(R.string.deleteFolderSuccessful),
                                CustomToast.LONG,
                                CustomToast.SUCCESS
                            ).show()
                            UserM.setUserData(it)
                        }
                    }
                } else {
                    CustomToast(this@FolderClickActivity).makeText(
                        this@FolderClickActivity,
                        resources.getString(R.string.deleteFolderErr),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                }
            } catch (e: Exception) {
                CustomToast(this@FolderClickActivity).makeText(
                    this@FolderClickActivity,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(this, null, msg)
    }
}