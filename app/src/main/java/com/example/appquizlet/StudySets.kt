package com.example.appquizlet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.databinding.FragmentStudySetsBinding
import com.example.appquizlet.model.StudySetItemData


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
        val listStudySet = mutableListOf<StudySetItemData>()
        listStudySet.add(StudySetItemData("Everyday word 1", 3, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 2", 15, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 3", 5, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))

        val adapterStudySet = RvStudySetItemAdapter(listStudySet)
        val rvStudySet = binding.rvStudySet
        rvStudySet.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvStudySet.adapter = adapterStudySet
    }
}