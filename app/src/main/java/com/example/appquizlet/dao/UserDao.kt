package com.example.appquizlet.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appquizlet.model.UserResponse

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserResponse)

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: String): UserResponse?
}
