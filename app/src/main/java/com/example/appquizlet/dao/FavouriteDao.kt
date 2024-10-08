package com.example.appquizlet.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appquizlet.entity.NewWord

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteWord(favouriteWord: NewWord): Long

    @Query("SELECT * FROM favourite_words")
    fun getAllFavouriteWords(): LiveData<List<NewWord>>

    @Query("DELETE FROM favourite_words WHERE id = :id")
    suspend fun deleteFavouriteWord(id: Int)


}