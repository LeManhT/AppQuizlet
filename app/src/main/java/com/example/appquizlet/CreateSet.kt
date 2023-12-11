package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
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
import kotlinx.coroutines.launch
import java.util.Collections

class CreateSet : AppCompatActivity(), CreateSetItemAdapter.OnIconClickListener {
    private lateinit var binding: ActivityCreateSetBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiService: ApiService
    private var listSet = mutableListOf<FlashCardModel>()
    private lateinit var adapterCreateSet: CreateSetItemAdapter

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
        adapterCreateSet = CreateSetItemAdapter(listSet)
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

            // Kiểm tra xem updatedList có dữ liệu hay không
            if (updatedList.isNotEmpty()) {
                // Chuyển danh sách thành chuỗi JSON để đẩy vào API
                // Gọi phương thức để tạo mới study set
                createNewStudySet(userId, name, desc, updatedList)
            } else {
                // Nếu danh sách rỗng, thông báo cho người dùng
                CustomToast(this).makeText(
                    this,
                    "Please add at least one flashcard.",
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
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
                Log.d("ar", body.toString())

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

}