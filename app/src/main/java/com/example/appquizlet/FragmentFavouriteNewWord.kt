package com.example.appquizlet

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.AdapterFavouriteWord
import com.example.appquizlet.databinding.FragmentFavouriteNewWordBinding
import com.example.appquizlet.entity.NewWord
import com.example.appquizlet.viewmodel.favourite.FavouriteNewWordViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentFavouriteNewWord : Fragment() {
    private lateinit var binding: FragmentFavouriteNewWordBinding
    private val favouriteViewModel: FavouriteNewWordViewModel by viewModels()
    private lateinit var adapterFavourite: AdapterFavouriteWord

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteNewWordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouriteViewModel.getAllFavouriteWords()

        favouriteViewModel.favouriteNewWords.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvListFavouriteWords.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            } else {
                binding.rvListFavouriteWords.visibility = View.VISIBLE
                binding.layoutNoData.visibility = View.GONE
            }
            adapterFavourite.setFavouriteWords(it)
        }

        adapterFavourite = AdapterFavouriteWord(object : AdapterFavouriteWord.IFavouriteClick {
            override fun onRemoveClick(favouriteWord: NewWord) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.are_you_sure_to_delete_word_from_favourite))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                        favouriteViewModel.deleteFavouriteWord(favouriteWord)
                    }
                    .show()
            }

            override fun onWordClick(favouriteWord: NewWord) {
                AlertDialog.Builder(requireContext())
                    .setTitle(favouriteWord.word)
                    .setMessage(favouriteWord.meaning)
                    .setPositiveButton("OK", null)
                    .show()
            }
        })

        binding.rvListFavouriteWords.apply {
            adapter = adapterFavourite
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

}