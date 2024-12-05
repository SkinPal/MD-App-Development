package com.capstone.skinpal.ui.product

import androidx.room.TypeConverter
import com.capstone.skinpal.data.remote.response.SkinConditions
import com.capstone.skinpal.data.remote.response.SkinTypes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSkinConditions(skinConditions: SkinConditions): String {
        return gson.toJson(skinConditions)
    }

    @TypeConverter
    fun toSkinConditions(skinConditionsString: String): SkinConditions {
        return gson.fromJson(skinConditionsString, SkinConditions::class.java)
    }

    @TypeConverter
    fun fromSkinTypes(skinTypes: SkinTypes): String {
        return gson.toJson(skinTypes)
    }

    @TypeConverter
    fun toSkinTypes(skinTypesString: String): SkinTypes {
        return gson.fromJson(skinTypesString, SkinTypes::class.java)
    }
}