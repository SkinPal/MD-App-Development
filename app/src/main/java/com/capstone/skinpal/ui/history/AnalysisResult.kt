package com.capstone.skinpal.ui.history

import com.capstone.skinpal.data.remote.response.FacialWashItem
import com.capstone.skinpal.data.remote.response.MoisturizerItem
import com.capstone.skinpal.data.remote.response.SerumItem
import com.capstone.skinpal.data.remote.response.SunscreenItem
import com.capstone.skinpal.data.remote.response.TonerItem
import com.capstone.skinpal.data.remote.response.TreatmentItem

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
    val timestamp: Long = System.currentTimeMillis(),
    val moisturizer: List<MoisturizerItem>,
    val treatment: List<TreatmentItem>,
    val sunscreen: List<SunscreenItem>,
    val toner: List<TonerItem>,
    val serum: List<SerumItem>,
    val facialWash: List<FacialWashItem>,
    val percentage: String?,
    val message: String?
)
