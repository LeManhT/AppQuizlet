package com.example.appquizlet

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.appquizlet.databinding.ActivityQuoteInLanguageBinding
import com.example.appquizlet.model.MainViewModelFactory
import com.example.appquizlet.model.MyViewModel
import com.example.appquizlet.roomDatabase.QuoteDatabase
import com.google.gson.Gson
import androidx.lifecycle.Observer


class QuoteInLanguage : AppCompatActivity() {
    lateinit var binding: ActivityQuoteInLanguageBinding
    private lateinit var quoteDatabase: QuoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_in_language)
        val repository = (application as QuoteApplication).remoteQuoteRepository

//        quoteDatabase = QuoteDatabase.getDatabase(this)
//        val quoteDao = quoteDatabase.quoteDao()
//        val quoteRepository = QuoteRepository(quoteDao)
        val myViewModel: MyViewModel by viewModels { MainViewModelFactory(repository) }
        myViewModel.quotes.observe(this) {
            Toast.makeText(this@QuoteInLanguage, it.results.size.toString(), Toast.LENGTH_SHORT).show()
            Log.d("dataQuote", Gson().toJson(it))
        }

//            GlobalScope.launch {
//                quoteDatabase.quoteDao().insertQuote(
//                    QuoteEntity(
//                        textQuote = "hehehe",
//                        quoteAuthor = "cong tuan2",
//                        userId = "12"
//                    )
//                )
//            }

    }

//    fun getQuote(view: View) {
//        quoteDatabase.quoteDao().getQuotes("12").observe(this) {
//            Log.d("dataQuote", Gson().toJson(it))
//        }
//    }
}