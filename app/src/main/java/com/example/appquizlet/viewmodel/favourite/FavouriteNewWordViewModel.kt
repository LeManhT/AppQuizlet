package com.example.appquizlet.viewmodel.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.entity.NewWord
import com.example.appquizlet.repository.favourite.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteNewWordViewModel @Inject constructor(private val favouriteNewWordRepository: FavouriteRepository) :
    ViewModel() {
    private val listFavouriteNewWords = MutableLiveData<List<NewWord>>()
    val favouriteNewWords: MutableLiveData<List<NewWord>> = listFavouriteNewWords

    private val editStatus = MutableLiveData<Long>()
    val editStatusLiveData: MutableLiveData<Long> = editStatus

    fun getAllFavouriteWords() {
        favouriteNewWordRepository.getAllFavouriteWords().observeForever {
            listFavouriteNewWords.postValue(it)
        }
    }

    fun addFavouriteWord(word: String, meaning: String) {
        viewModelScope.launch {
            val id = favouriteNewWordRepository.addFavouriteWord(word, meaning)
            editStatusLiveData.postValue(id)
        }
    }
    fun deleteFavouriteWord(word : NewWord) {
        viewModelScope.launch {
            favouriteNewWordRepository.deleteFavouriteWord(word)
        }

    }

}