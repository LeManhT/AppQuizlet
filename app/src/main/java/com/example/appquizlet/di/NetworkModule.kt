package com.example.appquizlet.di

import android.content.Context
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.QuoteApiService
import com.example.appquizlet.dao.FavouriteDao
import com.example.appquizlet.dao.QuoteDao
import com.example.appquizlet.dao.StoryDao
import com.example.appquizlet.repository.QuoteRepository
import com.example.appquizlet.repository.story.StoryRepository
import com.example.appquizlet.roomDatabase.QuoteDatabase
import com.example.appquizlet.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val baseUrl = Constants.baseUrl
    private val credentials = Credentials.basic("11167378", "60-dayfreetrial")
    private const val baseQuoteUrl = Constants.baseUrlQuote

    @Provides
    @Singleton
    @Named("mainRetrofit")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    @Named("quoteRetrofit")
    fun provideQuoteRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(baseQuoteUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    fun provideApiService(@Named("mainRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideApiQuoteService(@Named("quoteRetrofit") retrofit: Retrofit): QuoteApiService {
        return retrofit.create(QuoteApiService::class.java)
    }


//


//    @Provides
//    fun provideUserRepository(apiService: ApiService): UserRepository {
//        return UserRepository(apiService)
//    }
//
//    @Provides
//    fun provideHomeRepository(apiService: ApiService): HomeRepository {
//        return HomeRepository(apiService)
//    }
//
//    @Provides
//    fun provideDocumentRepository(apiService: ApiService): DocumentRepository {
//        return DocumentRepository(apiService)
//    }

    @Provides
    @Singleton
    fun provideMyAppDatabase(@ApplicationContext context: Context): QuoteDatabase {
        return QuoteDatabase.getInstance(context)
    }

    @Provides
    fun provideQuoteRepository(
        quoteApiService: QuoteApiService,
        quoteDb: QuoteDatabase,
        @ApplicationContext context: Context
    ): QuoteRepository {
        return QuoteRepository(quoteApiService, quoteDb, context)
    }


    @Provides
    fun provideStoryRepository(
        quoteDb: QuoteDatabase,
    ): StoryRepository {
        return StoryRepository(quoteDb)
    }


    @Provides
    fun provideQuoteDao(database: QuoteDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    fun provideStoryDao(database: QuoteDatabase): StoryDao {
        return database.storyDao()
    }

    @Provides
    fun provideFavouriteNewWordDao(database: QuoteDatabase): FavouriteDao {
        return database.favouriteNewWordDao()
    }

}