package com.example.appquizlet.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appquizlet.api.retrofit.QuoteApiService
import com.example.appquizlet.model.QuoteResponse
import com.example.appquizlet.roomDatabase.QuoteDatabase
import com.example.appquizlet.util.NetworkUtil
import com.google.gson.Gson

class RemoteQuoteRepository(
    private val quoteService: QuoteApiService,
    private val quoteDb: QuoteDatabase,
    private val context: Context
) {
    private val quoteRemoteLiveData = MutableLiveData<QuoteResponse>()
    val quotes: LiveData<QuoteResponse>
        get() = quoteRemoteLiveData

    suspend fun getQuotes(page: Int) {
        if (NetworkUtil.isInternetAvailable(context)) {
            val result = quoteService.getQuoteFromServer(page)
            Log.d("dataQuote12", Gson().toJson(result.body()))
            if (result.body() != null) {
                quoteDb.quoteDao().insertManyQuotes(result.body()!!.results)
                quoteRemoteLiveData.postValue(result.body())
            }
        } else {
            Log.d("dataQuote13", "vaoooooooo")
            val quotes = quoteDb.quoteDao().getQuotes()
            val quoteList = QuoteResponse(1, 1, 1, 1, 1, quotes)
            quoteRemoteLiveData.postValue(quoteList)
        }
        Log.d("dataQuote14", "vaoooooooo")
    }
}