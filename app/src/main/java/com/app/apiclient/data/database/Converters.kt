package com.app.apiclient.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        return try {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(value, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
