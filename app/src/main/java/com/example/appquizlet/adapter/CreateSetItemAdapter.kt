package com.example.appquizlet.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.CreateSetModel


class CreateSetItemAdapter(private val listSet: MutableList<CreateSetModel>) :
    RecyclerView.Adapter<CreateSetItemAdapter.CreateSetItemHolder>() {

    class CreateSetItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSetItemHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_flashcard, parent, false)
        return CreateSetItemHolder(view)
    }

    override fun onBindViewHolder(holder: CreateSetItemHolder, position: Int) {
        holder.itemView.apply {
            val txtTerm = findViewById<EditText>(R.id.edtTerm)
            val txtDefinition = findViewById<EditText>(R.id.edtDefinition)
            var currentItem = listSet[position]
            // Convert the String to Editable
            val editableTerm =
                Editable.Factory.getInstance().newEditable(currentItem.term)
            val editableDesc =
                Editable.Factory.getInstance().newEditable(currentItem.definition)
            txtTerm.text = editableTerm
            txtDefinition.text = editableDesc

            // Lắng nghe sự kiện khi dữ liệu thay đổi trong EditText
            txtTerm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Không cần thực hiện gì ở đây
                }

                override fun onTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    // Cập nhật dữ liệu trong listSet khi có sự thay đổi
                    currentItem.term = charSequence.toString()
                }

                override fun afterTextChanged(editable: Editable?) {
                    // Không cần thực hiện gì ở đây
                }
            })

            txtDefinition.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Không cần thực hiện gì ở đây
                }

                override fun onTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    // Cập nhật dữ liệu trong listSet khi có sự thay đổi
                    currentItem.definition = charSequence.toString()
                }

                override fun afterTextChanged(editable: Editable?) {
                    // Không cần thực hiện gì ở đây
                }
            })
        }

    }

    override fun getItemCount(): Int {
        return listSet.size
    }

    fun getListSet(): List<CreateSetModel> {
        return listSet.toList()
    }
}