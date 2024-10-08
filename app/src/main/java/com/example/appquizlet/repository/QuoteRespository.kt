package com.example.appquizlet.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appquizlet.api.retrofit.QuoteApiService
import com.example.appquizlet.dao.QuoteDao
import com.example.appquizlet.entity.QuoteEntity
import com.example.appquizlet.model.QuoteResponse
import com.example.appquizlet.roomDatabase.QuoteDatabase
import com.example.appquizlet.util.NetworkUtil
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val quoteService: QuoteApiService,
    private val quoteDb: QuoteDatabase,
    private val context: Context
) {
    private val quoteRemoteLiveData = MutableLiveData<QuoteResponse>()
    private val quoteLocalLiveData = MutableLiveData<List<QuoteEntity>>()
    val quotes: LiveData<QuoteResponse>
        get() = quoteRemoteLiveData

    val localQuotes: LiveData<List<QuoteEntity>>
        get() = quoteLocalLiveData

    suspend fun getQuotes(page: Int) {
        Log.d("dataQuote12", "vào hàm getQuotes")
        if (NetworkUtil.isInternetAvailable(context)) {
            val result = quoteService.getQuoteFromServer(page)
            Log.d("dataQuote12", Gson().toJson(result.body()))
            if (result.body() != null) {
                quoteRemoteLiveData.postValue(result.body())
            }
        } else {
            val quotes = quoteDb.quoteDao().getQuotes()
            val quoteList = QuoteResponse(1, 1, 1, 1, 1, quotes)
            quoteRemoteLiveData.postValue(quoteList)
        }
    }

    suspend fun insertQuote(quoteModel: QuoteEntity) {
        quoteDb.quoteDao().insertQuote(quoteModel)
    }

    suspend fun deleteLocalQuote(quoteId: Long) {
        quoteDb.quoteDao().deleteQuote(quoteId)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getLocalQuotes(userId: String) {
        val localQuote = quoteDb.quoteDao().getLocalQuotes(userId)
        GlobalScope.launch(Dispatchers.Main) {
            localQuote.observeForever { quoteEntities ->
                quoteLocalLiveData.postValue(quoteEntities)
            }
        }
    }
}