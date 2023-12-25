package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.adapter.CreateSetItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityCreateSetBinding
import com.example.appquizlet.model.CreateSetRequest
import com.example.appquizlet.model.FlashCardModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.Collections
import java.util.Locale

class CreateSet : AppCompatActivity(), CreateSetItemAdapter.OnIconClickListener {
    private lateinit var binding: ActivityCreateSetBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiService: ApiService
    private var listSet = mutableListOf<FlashCardModel>()
    private lateinit var adapterCreateSet: CreateSetItemAdapter
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var speechRecognitionPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivityCreateSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại

        listSet.add(FlashCardModel())
        listSet.add(FlashCardModel())
        listSet.add(FlashCardModel())
        listSet.add(FlashCardModel())
        adapterCreateSet = CreateSetItemAdapter(listSet)
        adapterCreateSet.setOnIconClickListener(this)
        binding.RvCreateSets.layoutManager = LinearLayoutManager(this)
        binding.RvCreateSets.adapter = adapterCreateSet

        binding.addNewCard.setOnClickListener {
            listSet.add(FlashCardModel())
            adapterCreateSet.notifyItemInserted(listSet.size - 1)

            binding.RvCreateSets.scrollToPosition(listSet.size - 1)

            binding.createSetScrollView.post {
                binding.createSetScrollView.smoothScrollTo(0, binding.RvCreateSets.bottom)
            }

        }
        binding.iconTickCreateSet.setOnClickListener {
            val name = binding.txtNameStudySet.text.toString()
            val desc = binding.txtDescription.toString()
            val userId = Helper.getDataUserId(this)

            // Lấy danh sách CreateSetModel từ adapter
            val updatedList = adapterCreateSet.getListSet()
            val isEmptyItemExist =
                updatedList.any { it.term?.isEmpty() == true || it.definition?.isEmpty() == true }

            // Kiểm tra xem updatedList có dữ liệu hay không
            if (isEmptyItemExist) {
                CustomToast(this).makeText(
                    this,
                    "Please fill in all flashcards before updating.",
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else if (updatedList.isNotEmpty()) {
                if (updatedList.size < 4) {
                    CustomToast(this).makeText(
                        this,
                        "Please add at least 4 flashcards.",
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                } else {
                    if (name.isEmpty()) {
                        CustomToast(this).makeText(
                            this,
                            resources.getString(R.string.set_name_is_required),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    } else {
                        createNewStudySet(userId, name, desc, updatedList)
                    }
                }
            }
        }


//        setDragDropItem(listSet, binding.RvCreateSets)

        binding.txtDesc.setOnClickListener {
            addSecondLayout()
        }

        binding.iconSetting.setOnClickListener {
            val intent = Intent(this, SetOptionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createNewStudySet(
        userId: String,
        studySetName: String,
        studySetDesc: String,
        dataSet: List<FlashCardModel>
    ) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.creatingStudySet))
            try {
//                val gson = Gson()
//                val dataSetArray = JsonArray()
//                dataSet.forEach {
//                    dataSetArray.add(gson.toJsonTree(it))
//                }
//                val body = JsonObject().apply {
//                    addProperty(resources.getString(R.string.createFolderNameField), studySetName)
//                    addProperty(resources.getString(R.string.descriptionField), studySetDesc)
//                    add(
//                        resources.getString(R.string.allNewCards),
//                        dataSetArray
//                    )
//                }

                val body = CreateSetRequest(
                    name = studySetName,
                    description = studySetDesc,
                    allNewCards = dataSet
                )
                val result = apiService.createNewStudySet(userId, body)

                if (result.isSuccessful) {
                    result.body()?.let {
                        this@CreateSet.let { it1 ->
                            CustomToast(it1).makeText(
                                this@CreateSet,
                                resources.getString(R.string.create_folder_success),
                                CustomToast.LONG,
                                CustomToast.SUCCESS
                            ).show()
                            UserM.setUserData(it)
                        }
                        val i = Intent(this@CreateSet, MainActivity_Logged_In::class.java)
                        startActivity(i)
                    }
                } else {
                    result.errorBody()?.string()?.let {
                        this@CreateSet.let { it1 ->
                            CustomToast(it1).makeText(
                                this@CreateSet,
                                it,
                                CustomToast.LONG,
                                CustomToast.ERROR
                            ).show()
                        }
                        Log.d("err", it)
                    }
                }
            } catch (e: Exception) {
                CustomToast(this@CreateSet).makeText(
                    this@CreateSet,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
                Log.d("err2", e.message.toString())
            } finally {
                progressDialog.dismiss()
            }
        }
    }


    private fun addSecondLayout() {
        binding.layoutDesc.visibility = View.VISIBLE
        val params = binding.createSetScrollView.layoutParams as RelativeLayout.LayoutParams
        params.topMargin =
            resources.getDimensionPixelSize(R.dimen.h_200) + 200 // Add the additional margin
        binding.createSetScrollView.layoutParams = params
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

    override fun onIconClick(position: Int) {
        Log.d("startSpeechRecognition1", "click")
        speechRecognitionPosition = position
        startSpeechRecognition(position)
    }


    override fun onDeleteClick(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            listSet.removeAt(position)
            adapterCreateSet.notifyItemRemoved(position)
            adapterCreateSet.notifyDataSetChanged()
        }
    }

    override fun onAddNewCard(position: Int) {
        val newItem = FlashCardModel()
        newItem.id = ""
        listSet.add(position + 1, newItem)
        adapterCreateSet.notifyItemInserted(position + 1)
        adapterCreateSet.notifyDataSetChanged()
    }

    private fun setDragDropItem(list: MutableList<FlashCardModel>, recyclerView: RecyclerView) {
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    Collections.swap(list, fromPos, toPos)
                    recyclerView.adapter!!.notifyItemMoved(fromPos, toPos)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }

                override fun onChildDraw(
                    c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
                ) {
//                    val itemView = viewHolder.itemView

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    recyclerView.invalidate()

                    recyclerView.invalidate()
                }

            }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(this, null, msg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val matches: ArrayList<String>? = data?.getStringArrayListExtra(
            RecognizerIntent.EXTRA_RESULTS
        )

        Log.d("goi", Gson().toJson(matches))

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val position = speechRecognitionPosition

            if (position != -1) {
                val speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                if (!speechResults.isNullOrEmpty()) {
                    val spokenText = speechResults[0]
                    if (adapterCreateSet.getIsDefinition() == true) {
                        updateRecyclerViewItemDefinition(position, spokenText)
                    } else {
                        updateRecyclerViewItemTerm(position, spokenText)
                    }
                } else {
                    Toast.makeText(this, "No speech results found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("onActivityResult", "Position is not available in the intent")
            }
            speechRecognitionPosition = -1
        }
    }


    private fun updateRecyclerViewItemTerm(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item = listSet[position]
            item.term = spokenText
            adapterCreateSet.notifyItemChanged(position)
        }
    }

    private fun updateRecyclerViewItemDefinition(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item = listSet[position]
            item.definition = spokenText
            adapterCreateSet.notifyItemChanged(position)
        }
    }

    private fun startSpeechRecognition(position: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        intent.putExtra(RecognizerIntent.EXTRA_ORIGIN, position)


        Log.d("startSpeechRecognition", "Intent extras: ${intent.extras}")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}