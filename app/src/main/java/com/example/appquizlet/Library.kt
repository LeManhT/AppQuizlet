package com.example.appquizlet

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.adapter.ViewPagerLibAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.FragmentLibraryBinding
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonObject
import kotlinx.coroutines.launch


class Library : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog
    private lateinit var adapterLibPager: ViewPagerLibAdapter
    private var receivedData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        binding = FragmentLibraryBinding.inflate(inflater, container, false)


        //        Adapter
        adapterLibPager =
            ViewPagerLibAdapter(childFragmentManager, lifecycle)
        binding.pagerLib.adapter = adapterLibPager
        TabLayoutMediator(binding.tabLib, binding.pagerLib) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = resources.getString(R.string.lb_study_sets)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.note, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                    UserM.getUserData().observe(viewLifecycleOwner) {
                        badge.number = Helper.getAllStudySets(it).size
                    }
                }

                1 -> {
                    tab.text = resources.getString(R.string.folders)
                    tab.icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.folder_outlined, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                    UserM.getUserData().observe(viewLifecycleOwner) {
                        badge.number = it.documents.folders.size
                    }
                }
                //
                //                2 -> {
                //                    tab.text = resources.getString(R.string.lb_classes)
                //                    tab.icon =
                //                        ResourcesCompat.getDrawable(resources, R.drawable.resource_class, null)
                //                    val badge = tab.orCreateBadge
                //                    badge.backgroundColor =
                //                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                //                    badge.number = 0
                //                }
            }
        }.attach()

        binding.txtLibPlus.setOnClickListener {

            val currentItem = binding.pagerLib.currentItem
            //            Log.d(
            //                "tf",
            //                "currentItem: $currentItem, fragments size: ${childFragmentManager.fragments.size}"
            //            )
            if (currentItem < childFragmentManager.fragments.size) {
                val currentFragment = childFragmentManager.fragments[currentItem]
                //                Log.d("tf", "$currentItem $currentFragment")
                if (currentFragment is FoldersFragment) {
                    showCustomDialog(
                        resources.getString(R.string.add_folder),
                        "",
                        resources.getString(R.string.folder_name),
                        resources.getString(R.string.desc_optional)
                    )
                }
                if (currentFragment is StudySets) {
                    val intent = Intent(context, CreateSet::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.txtBack.setOnClickListener {
            val i = Intent(context, MainActivity_Logged_In::class.java)
            startActivity(i)
        }


        binding.tabLib.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {

                    }

                    1 -> {
                        //                        val add = Add()
                        //                        add.showCustomDialog(
                        //                            resources.getString(R.string.add_folder),
                        //                            "",
                        //                            resources.getString(R.string.folder_name),
                        //                            resources.getString(R.string.desc_optional)
                        //                        )
                    }

                    2 -> {}
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        if (receivedData == "createFolder" || receivedData == "viewAllFolder") {
            binding.pagerLib.currentItem = 1
        } else if (receivedData == "createSet" || receivedData.isEmpty() || receivedData == "") {
            binding.pagerLib.currentItem = 0
        }

        return binding.root
    }


    companion object {
        const val TAG = "LibraryT"
        private var instance: Library? = null

        fun newInstance(): Library {
            if (instance == null) {
                instance = Library()
            }
            return instance!!
        }
    }


    private fun showCustomDialog(
        title: String,
        content: String,
        edtPlaceholderFolderName: String,
        edtPlaceholderDesc: String
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        // Tạo layout cho dialog
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40)
        if (!content.isEmpty()) {
            //            builder.setMessage(content)
            val textContent = TextView(context)
            textContent.setText(content)
            textContent.setPadding(10, 0, 10, 0)
            layout.addView(textContent)
        }
        // Tạo EditText
        val editTextFolder = createEditTextWithCustomBottomBorder(edtPlaceholderFolderName)
        //        editTextFolder.hint = edtPlaceholderFolderName
        layout.addView(editTextFolder)

        val editTextDesc = createEditTextWithCustomBottomBorder(edtPlaceholderDesc)
        //        editTextDesc.hint = edtPlaceholderDesc
        layout.addView(editTextDesc)

        builder.setView(layout)

        builder.setPositiveButton("OK") { dialog, _ ->
            val inputText = editTextFolder.text.toString()
            val description = editTextDesc.text.toString()
            // Xử lý dữ liệu từ EditText sau khi người dùng nhấn OK
            // Ví dụ: Hiển thị nó hoặc thực hiện các tác vụ khác
            // ở đây
            createNewFolder(inputText, description, Helper.getDataUserId(requireContext()))
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Xử lý khi người dùng nhấn Cancel
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun createEditTextWithCustomBottomBorder(hint: String): EditText {
        val editText = EditText(context)
        editText.hint = hint

        // Custom drawable for bottom border
        val defaultBottomBorder = GradientDrawable()
        defaultBottomBorder.setColor(Color.BLACK) // Set your desired border color
        defaultBottomBorder.setSize(0, 5) // Set your desired border height

        val focusBottomBorder = GradientDrawable()
        focusBottomBorder.setColor(Color.BLUE) // Set your desired focus border color
        focusBottomBorder.setSize(0, 10) // Set your desired focus border height

        // Set layout parameters with margins
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 10
        layoutParams.bottomMargin = 10
        editText.layoutParams = layoutParams


        //        editText.background = defaultBottomBorder

        // Set a listener to change the border color when focused
        //        editText.setOnFocusChangeListener { view, hasFocus ->
        //            editText.background = if (hasFocus) focusBottomBorder else defaultBottomBorder
        //        }

        return editText
    }

    private fun createNewFolder(name: String, description: String = "", userId: String) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.creating))
            try {
                val body = JsonObject().apply {
                    addProperty(resources.getString(R.string.createFolderNameField), name)
                    addProperty(resources.getString(R.string.descriptionField), description)
                }
                val result = apiService.createNewFolder(userId, body)
                if (result.isSuccessful) {
                    result.body().let { it ->
                        if (it != null) {
                            context?.let { it1 ->
                                CustomToast(it1).makeText(
                                    requireContext(),
                                    resources.getString(R.string.create_folder_success),
                                    CustomToast.LONG,
                                    CustomToast.SUCCESS
                                ).show()
                                UserM.setUserData(it)
                                val i = Intent(requireContext(), MainActivity_Logged_In::class.java)
                                i.putExtra("selectedFragment", "Library")
                                i.putExtra("createMethod", "createFolder")
                                startActivity(i)
                            }
                        }
                    }
                } else {
                    result.errorBody()?.string()?.let {
                        context?.let { it1 ->
                            CustomToast(it1).makeText(
                                requireContext(),
                                it,
                                CustomToast.LONG,
                                CustomToast.ERROR
                            ).show()
                        }
                        Log.d("err", it)
                    }
                }
            } catch (e: Exception) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
                Log.e("Failed", e.toString())
            } finally {
                if (isAdded) {
                    progressDialog.dismiss()
                }
            }

        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(context, null, msg)
    }

    fun setDataMethod(method: String) {
        receivedData = method
    }


    override fun onDestroy() {
        super.onDestroy()
        receivedData = ""
    }

}