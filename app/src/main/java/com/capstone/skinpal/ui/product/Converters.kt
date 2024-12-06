package com.capstone.skinpal.ui.product

import androidx.room.TypeConverter
import com.capstone.skinpal.data.remote.response.SkinConditions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    fun skinConditionsToJson(skinConditions: SkinConditions): String {
        return gson.toJson(skinConditions)
    }

    fun jsonToSkinConditions(json: String): SkinConditions {
        return gson.fromJson(json, SkinConditions::class.java)
    }

    fun recommendationsToJson(recommendations: List<String>): String {
        return gson.toJson(recommendations)
    }

    fun jsonToRecommendations(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}