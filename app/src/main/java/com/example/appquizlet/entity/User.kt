package com.example.appquizlet.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.appquizlet.model.DocumentModel
import com.example.appquizlet.typeConverter.DataConverter

@Entity
data class User(
    @PrimaryKey
    val id: String,
    val seqId: Int,
    val loginName: String,
    val loginPassword: String,
    val userName: String,
    val email: String,
    val dateOfBirth: String,
    val timeCreated: Long,
    @TypeConverters(DataConverter::class)
    val documents: DocumentModel,
    val darkMode: Boolean,
    val notification: Boolean
)
