package com.pobedie.ohmyping.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pobedie.ohmyping.entity.VibrationPattern
import java.io.ByteArrayOutputStream
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


    @TypeConverter
    fun fromBase64ToBitmap(base64String: String?): Bitmap? {
        if (base64String.isNullOrEmpty()) return null

        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    fun fromBitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) return null

        return try {
            val outputStream = ByteArrayOutputStream()
            // Compress bitmap to PNG format (you can use JPEG for smaller size)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    fun fromVibrationPatternToString(vibration: VibrationPattern): String = vibration.name

    @TypeConverter
    fun fromStringToVibrationPattern(vibration: String): VibrationPattern = try {
        VibrationPattern.valueOf(vibration)
    } catch (e: IllegalArgumentException) {
        Log.e("DB Converters", e.toString())
        VibrationPattern.BeeHive
    }

}