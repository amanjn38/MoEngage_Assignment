package com.example.moengageassignment.roomhelper

import androidx.room.TypeConverter
import com.example.moengageassignment.models.Source
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): Source? {
        // Convert the JSON string to a Source object
        return value?.let {
            gson.fromJson(it, Source::class.java)
        }
    }

    @TypeConverter
    fun toString(source: Source?): String? {
        // Convert the Source object to a JSON string
        return source?.let {
            gson.toJson(it)
        }
    }
}