package com.capstone.skinpal.ui.product

import androidx.room.TypeConverter
import com.capstone.skinpal.data.remote.response.FacialWashItem
import com.capstone.skinpal.data.remote.response.MoisturizerItem
import com.capstone.skinpal.data.remote.response.SerumItem
import com.capstone.skinpal.data.remote.response.SunscreenItem
import com.capstone.skinpal.data.remote.response.TonerItem
import com.capstone.skinpal.data.remote.response.TreatmentItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromMoisturizerList(value: List<MoisturizerItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMoisturizerList(value: String): List<MoisturizerItem> {
        val type = object : TypeToken<List<MoisturizerItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromTreatmentList(value: List<TreatmentItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTreatmentList(value: String): List<TreatmentItem> {
        val type = object : TypeToken<List<TreatmentItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromSerumList(value: List<SerumItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSerumList(value: String): List<SerumItem> {
        val type = object : TypeToken<List<SerumItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromTonerList(value: List<TonerItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTonerList(value: String): List<TonerItem> {
        val type = object : TypeToken<List<TonerItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromSunscreenList(value: List<SunscreenItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSunscreenList(value: String): List<SunscreenItem> {
        val type = object : TypeToken<List<SunscreenItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromFacialWashList(value: List<FacialWashItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFacialWashList(value: String): List<FacialWashItem> {
        val type = object : TypeToken<List<FacialWashItem>>() {}.type
        return gson.fromJson(value, type)
    }
}
