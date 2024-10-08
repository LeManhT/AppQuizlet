package com.example.appquizlet.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appquizlet.dao.FavouriteDao
import com.example.appquizlet.dao.QuoteDao
import com.example.appquizlet.entity.NewWord
import com.example.appquizlet.entity.QuoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [QuoteEntity::class, NewWord::class],
    version = 1,
    exportSchema = true
)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun favouriteNewWordDao(): FavouriteDao

    companion object {
        @Volatile
        private var instance: QuoteDatabase? = null

        fun getInstance(context: Context): QuoteDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, QuoteDatabase::class.java, "quotes.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val applicationScope = CoroutineScope(Dispatchers.IO)
                        applicationScope.launch {

                        }
                    }
                })
                .build()
    }
}