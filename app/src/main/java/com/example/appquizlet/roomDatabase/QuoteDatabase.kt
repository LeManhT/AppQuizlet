package com.example.appquizlet.roomDatabase

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appquizlet.dao.FavouriteDao
import com.example.appquizlet.dao.QuoteDao
import com.example.appquizlet.dao.StoryDao
import com.example.appquizlet.entity.NewWord
import com.example.appquizlet.entity.QuoteEntity
import com.example.appquizlet.entity.Story
import com.example.appquizlet.typeconverters.Converter
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [QuoteEntity::class, NewWord::class, Story::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun favouriteNewWordDao(): FavouriteDao
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var instance: QuoteDatabase? = null

        fun getInstance(context: Context): QuoteDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, QuoteDatabase::class.java, "quotes2.db")
//                .createFromAsset("stories.json")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val applicationScope = CoroutineScope(Dispatchers.IO)
                        applicationScope.launch {
                            // Đọc file JSON từ assets
                            val json = Helper.readJsonFromAsset(context, "stories.json")
                            json?.let {
                                // Parse dữ liệu JSON
                                val stories = Helper.parseStoriesFromJson(it)
                                // Lưu vào database
                                Log.d("ListStories", Gson().toJson(stories))
                                stories?.let { storyList ->
                                    Log.d("ListStoriesList", Gson().toJson(storyList))
                                    instance?.storyDao()?.insertAll(storyList)
                                }
                            }
                        }
                    }
                })
                .build()
    }
}