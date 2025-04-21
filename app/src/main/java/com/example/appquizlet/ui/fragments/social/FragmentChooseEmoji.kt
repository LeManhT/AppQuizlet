package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.EmotionAdapter
import com.example.appquizlet.databinding.FragmentChooseEmojiBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

//class FragmentChooseEmoji : Fragment() {
//    private lateinit var binding: FragmentChooseEmojiBinding
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentChooseEmojiBinding.inflate(layoutInflater, container, false)
//        return binding.root
//    }
//}

class EmotionPickerBottomSheet(private val onEmotionSelected: (String, String) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_choose_emoji, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvEmotions)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 cột giống ảnh bạn gửi
        recyclerView.adapter = EmotionAdapter(getEmotionsList()) { emoji, text ->
            onEmotionSelected(emoji, text)
            dismiss()
        }
    }

    private fun getEmotionsList(): List<Pair<String, String>> {
        return listOf(
            "😊" to "Hạnh phúc",
            "😢" to "Buồn",
            "😍" to "Được yêu",
            "😂" to "Hào hứng",
            "😜" to "Điên",
            "😌" to "Thư giãn",
            "🤩" to "Tuyệt vời",
            "😡" to "Giận dữ"
        )
    }
}
