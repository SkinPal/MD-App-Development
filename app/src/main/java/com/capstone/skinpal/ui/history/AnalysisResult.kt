package com.capstone.skinpal.ui.history

import com.capstone.skinpal.data.remote.response.FacialWashItem
import com.capstone.skinpal.data.remote.response.MoisturizerItem
import com.capstone.skinpal.data.remote.response.SerumItem
import com.capstone.skinpal.data.remote.response.SunscreenItem
import com.capstone.skinpal.data.remote.response.TonerItem
import com.capstone.skinpal.data.remote.response.TreatmentItem
import com.google.gson.annotations.SerializedName

data class AnalysisResult(
    val userId: String,
    val week: String,
    val skinType: String,
    val acne : String,
    val redness : String,
    val wrinkles: String,
    val recommendations: String,
    val imageUri: String?,
    val publicUrl: String,
    // Store as JSON string
    val timestamp: Long = System.currentTimeMillis(),
    @field:SerializedName("moisturizer")
    val moisturizer: List<MoisturizerItem>,

    @field:SerializedName("treatment")
    val treatment: List<TreatmentItem>,

    @field:SerializedName("sunscreen")
    val sunscreen: List<SunscreenItem>,

    @field:SerializedName("toner")
    val toner: List<TonerItem>,

    @field:SerializedName("serum")
    val serum: List<SerumItem>,

    @field:SerializedName("facial_wash")
    val facialWash: List<FacialWashItem>

)
