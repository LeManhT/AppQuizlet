package com.example.appquizlet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.SolutionItemAdapter
import com.example.appquizlet.databinding.FragmentSolutionBinding
import com.example.appquizlet.model.SolutionItemModel


class Solution : Fragment() {
    private lateinit var binding: FragmentSolutionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolutionBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listSolutionItems = mutableListOf<SolutionItemModel>()
        listSolutionItems.add(SolutionItemModel(R.drawable.three_book, 4, "Chemistry"))
        listSolutionItems.add(SolutionItemModel(R.drawable.three_book, 5, "Math"))
        listSolutionItems.add(SolutionItemModel(R.drawable.three_book, 6, "Science"))
        listSolutionItems.add(SolutionItemModel(R.drawable.three_book, 7, "Physic"))
        listSolutionItems.add(SolutionItemModel(R.drawable.three_book, 8, "Literature"))

        val solutionItemAdapter = SolutionItemAdapter(listSolutionItems)
        binding.rvSolution.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSolution.adapter = solutionItemAdapter
    }
}