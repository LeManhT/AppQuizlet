package com.example.appquizlet.typeConverter

import androidx.room.TypeConverter
import com.example.appquizlet.model.DocumentModel
import com.google.gson.Gson

class DataConverter {
    @TypeConverter
    fun fromDocuments(documents: DocumentModel): String {
        val gson = Gson()
        return gson.toJson(documents)
    }

    @TypeConverter
    fun toDocuments(json: String): DocumentModel {
        val gson = Gson()
        return gson.fromJson(json, DocumentModel::class.java)
    }
}
