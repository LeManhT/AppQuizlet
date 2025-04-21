package com.example.appquizlet.di

import android.content.Context
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.QuizletAIService
import com.example.appquizlet.dao.FavouriteDao
import com.example.appquizlet.dao.StoryDao
import com.example.appquizlet.repository.story.StoryRepository
import com.example.appquizlet.roomDatabase.QuoteDatabase
import com.example.appquizlet.services.SignalRFriendHubService
import com.example.appquizlet.services.SignalRService
import com.example.appquizlet.services.WebRTCManager
import com.example.appquizlet.util.Constants
import com.example.appquizlet.util.Helper
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
            return Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder().build())
                .build()
    }

    @Provides
    fun provideApiService(@Named("mainRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideQuizletAIService(@Named("quizletAIRetrofit") retrofit: Retrofit): QuizletAIService {
        return retrofit.create(QuizletAIService::class.java)
    }

    @Provides
    @Named("quizletAIRetrofit")
    fun provideQuizletAIRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.quizletAiBaseUrl) // Replace with your actual API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideMyAppDatabase(@ApplicationContext context: Context): QuoteDatabase {
        return QuoteDatabase.getInstance(context)
    }

    @Provides
    fun provideStoryRepository(
        quoteDb: QuoteDatabase,
    ): StoryRepository {
        return StoryRepository(quoteDb)
    }

    @Provides
    fun provideStoryDao(database: QuoteDatabase): StoryDao {
        return database.storyDao()
    }

    @Provides
    fun provideFavouriteNewWordDao(database: QuoteDatabase): FavouriteDao {
        return database.favouriteNewWordDao()
    }

    @Provides
    @Singleton
    fun provideSignalRService(): SignalRService {
        return SignalRService()
    }

    @Provides
    @Singleton
    fun provideSignalRFriendHubService(@ApplicationContext context: Context): SignalRFriendHubService {
        return SignalRFriendHubService(Helper.getDataUserId(context))
    }

    @Provides
    @Singleton
    fun provideWebRTCManager(
        @ApplicationContext context: Context,
        signalRService: SignalRService
    ): WebRTCManager {
        return WebRTCManager(context, signalRService)
    }

}