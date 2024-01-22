package com.example.appquizlet.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.entity.QuoteEntity
import com.example.appquizlet.repository.RemoteQuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(private val quoteRepository: RemoteQuoteRepository) : ViewModel() {

    //    val dataQuote: MutableLiveData<List<QuoteModel>> = MutableLiveData()

    val quotes : LiveData<QuoteResponse>
        get() = quoteRepository.quotes
    init {
        viewModelScope.launch(Dispatchers.IO) {
            quoteRepository.getQuotes(1)
        }
    }

//    fun getQuote(userId: String): LiveData<List<QuoteEntity>> {
//        return quoteRepository.getQuote(userId)
//    }
//
//    fun insertQuote(quoteModel: QuoteEntity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            quoteRepository.insertQuote(quoteModel)
//        }
//    }

//    fun updateQuote(quoteModel: QuoteEntity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            quoteRepository.updateQuote(quoteModel)
//        }
//    }

}