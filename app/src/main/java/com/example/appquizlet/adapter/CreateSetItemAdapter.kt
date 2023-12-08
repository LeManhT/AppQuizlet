package com.example.appquizlet.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.appquizlet.databinding.LayoutFlashcardBinding
import com.example.appquizlet.model.FlashCardModel
import com.google.gson.Gson


class CreateSetItemAdapter(
    private val listSet: MutableList<FlashCardModel>
) : RecyclerView.Adapter<CreateSetItemAdapter.CreateSetItemHolder>() {

    interface OnIconClickListener {
        fun onIconClick(position: Int)
        fun onDeleteClick(position: Int)

        fun onAddNewCard(position: Int)
    }

    private var onIconClickListener: OnIconClickListener? = null
    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    inner class CreateSetItemHolder(val binding: LayoutFlashcardBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val listNewItemInSet = mutableListOf<FlashCardModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSetItemHolder {
//        val binding = LayoutFlashcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreateSetItemHolder(
            LayoutFlashcardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: CreateSetItemHolder, position: Int) {
        viewBinderHelper.closeLayout(listSet[position].timeCreated.toString())
        val currentDateTime: java.util.Date = java.util.Date()
        val currentTimestamp: Long = currentDateTime.time
        viewBinderHelper.bind(
            holder.binding.swipeRevealLayout,
            listSet[position].timeCreated.toString()
        )

        val txtTerm = holder.binding.edtTerm
        val txtDefinition = holder.binding.edtDefinition
//        val layoutTerm = holder.binding.btnVoiceTerm
//        val layoutDefinition = holder.binding.btnVoiceDefinition

        holder.binding.textFieldDefinition.setEndIconOnClickListener {
            onIconClickListener?.onIconClick(position)
        }

        holder.binding.textFieldTerm.setEndIconOnClickListener {
            onIconClickListener?.onIconClick(position)
        }


//        layoutDefinition.setOnClickListener {
//            onIconClickListener?.onIconClick(position)
//        }
//
//        layoutTerm.setOnClickListener {
//            onIconClickListener?.onIconClick(position)
//        }

        holder.binding.btnAddNewCard.setOnClickListener {
            onIconClickListener?.onAddNewCard(position)
        }

        val currentItem = listSet[position]
        // Convert the String to Editable
        val editableTerm = Editable.Factory.getInstance().newEditable(currentItem.term)
        val editableDesc = Editable.Factory.getInstance().newEditable(currentItem.definition)
        txtTerm.text = editableTerm
        txtDefinition.text = editableDesc
        if (currentItem.isNew == true) {
            listNewItemInSet.add(currentItem)
        }

        // Lắng nghe sự kiện khi dữ liệu thay đổi trong EditText
        txtTerm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?, start: Int, count: Int, after: Int
            ) {
                // Không cần thực hiện gì ở đây
            }

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {
                // Cập nhật dữ liệu trong listSet khi có sự thay đổi

                listSet.getOrNull(holder.adapterPosition)?.term = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        txtDefinition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?, start: Int, count: Int, after: Int
            ) {
                // Không cần thực hiện gì ở đây
            }

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {
                // Cập nhật dữ liệu trong listSet khi có sự thay đổi
                listSet.getOrNull(holder.adapterPosition)?.definition = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {
//                if (listSet.getOrNull(holder.adapterPosition)?.term?.isEmpty() == true) {
//                    holder.binding.textFieldDefinition.apply {
//                        isErrorEnabled = true
//                        error = resources.getString(R.string.this_field_cannot_empty)
//                    }
//                }
            }
        })
        holder.binding.btnDeleteCard.setOnClickListener {
            onIconClickListener?.onDeleteClick(position)
        }
        Log.d("pos6", Gson().toJson(getListSet()))

    }


    // Add this function to start speech recognition


    // Add this function to handle the result of speech recognition
//    fun handleSpeechRecognitionResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Log.d("go","hahaha")
//        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                val res: ArrayList<String>? =
//                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as? ArrayList<String>
//
//                // Assuming that the user wants to update the term field
//                val positionToUpdate = 0 // Change this to the desired position
//                if (positionToUpdate < listSet.size && res != null && res.isNotEmpty()) {
//                    val spokenText = res[0]
//
//                    // Update both term and definition fields
//                    listSet[positionToUpdate].term = spokenText
//                    listSet[positionToUpdate].definition = spokenText
//
//                    // Notify the adapter that the data has changed
//                    notifyDataSetChanged()
//                }
//            }
//        }
//    }
    fun setOnIconClickListener(listener: OnIconClickListener) {
        this.onIconClickListener = listener
    }


    override fun getItemCount(): Int {
        return listSet.size
    }

    fun getListSet(): MutableList<FlashCardModel> {
        return listSet
    }

}