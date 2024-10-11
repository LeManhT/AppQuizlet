package com.example.appquizlet.typeconverters

import androidx.room.TypeConverter
import com.example.appquizlet.entity.NewWord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    @TypeConverter
    fun fromNewWordList(value: List<NewWord>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toNewWordList(value: String): List<NewWord> {
        val listType = object : TypeToken<List<NewWord>>() {}.type
        return Gson().fromJson(value, listType)
    }
}