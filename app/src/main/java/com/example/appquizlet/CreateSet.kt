package com.example.appquizlet

import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.adapter.CreateSetItemAdapter
import com.example.appquizlet.databinding.ActivityCreateSetBinding
import com.example.appquizlet.model.CreateSetModel
import java.util.Collections

class CreateSet : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivityCreateSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại

        val listSet = mutableListOf<CreateSetModel>()
        listSet.add(CreateSetModel())
        listSet.add(CreateSetModel())
        val adapterCreateSet = CreateSetItemAdapter(listSet)
        binding.RvCreateSets.layoutManager = LinearLayoutManager(this)
        binding.RvCreateSets.adapter = adapterCreateSet

        binding.addNewCard.setOnClickListener {
            listSet.add(CreateSetModel())
            adapterCreateSet.notifyItemInserted(listSet.size - 1)

            binding.RvCreateSets.scrollToPosition(listSet.size - 1)

            binding.createSetScrollView.post {
                binding.createSetScrollView.smoothScrollTo(0, binding.RvCreateSets.bottom)
            }

        }

        setDragDropItem(listSet, binding.RvCreateSets)

        binding.txtDesc.setOnClickListener {
            addSecondLayout()
        }

        binding.iconSetting.setOnClickListener {
            val intent = Intent(this, SetOptionActivity::class.java)
            startActivity(intent)
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

    private fun setDragDropItem(list: MutableList<CreateSetModel>, recyclerView: RecyclerView) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

}