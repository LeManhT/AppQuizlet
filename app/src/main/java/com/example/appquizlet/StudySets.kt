package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.databinding.FragmentStudySetsBinding
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.Gson


class StudySets : Fragment(R.layout.fragment_study_sets) {
    private lateinit var binding: FragmentStudySetsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudySetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listStudySet = mutableListOf<StudySetModel>()

        val adapterStudySet = RvStudySetItemAdapter(listStudySet, object : RVStudySetItem {
            override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                val intent = Intent(requireContext(), StudySetDetail::class.java)
                intent.putExtra("setId", listStudySet[position].id)
                startActivity(intent)
//                setItem.isSelected = !setItem.isSelected!!
            }

        })

        val userDataStudySet = UserM.getUserData()
        userDataStudySet.observe(viewLifecycleOwner) {
            listStudySet.clear()
            listStudySet.addAll(it.documents.studySets)
            if (listStudySet.isEmpty()) {
                Helper.replaceWithNoDataFragment(
                    requireFragmentManager(),
                    R.id.studySetItemContainer
                )
            }

            // Thông báo cho adapter rằng dữ liệu đã thay đổi để cập nhật giao diện người dùng
            adapterStudySet.notifyDataSetChanged()
        }


        val rvStudySet = binding.rvStudySet
        rvStudySet.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvStudySet.adapter = adapterStudySet
    }
}