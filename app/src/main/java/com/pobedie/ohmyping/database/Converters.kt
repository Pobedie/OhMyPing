package com.pobedie.ohmyping.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringToList(value: String): List<String> {
        val listType: Type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return gson.toJson(list)
    }
}