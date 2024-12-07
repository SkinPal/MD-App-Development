package com.capstone.skinpal.data.remote.response

import com.google.gson.annotations.SerializedName

/*data class AnalysisResponse(

	@field:SerializedName("tipe_kulit")
	val tipeKulit: String? = null,

	@field:SerializedName("wrinkles")
	val wrinkles: Any? = null,

	@field:SerializedName("score")
	val score: Any? = null,

	@field:SerializedName("acne")
	val acne: Any? = null,

	@field:SerializedName("redness")
	val redness: Any? = null,

	@field:SerializedName("recommendations")
	val recommendations: Recommendations? = null
)

data class SkinConditions(

	@field:SerializedName("wrinkles")
	val wrinkles: Boolean? = null,

	@field:SerializedName("acne")
	val acne: Boolean? = null,

	@field:SerializedName("redness")
	val redness: Boolean? = null
)

data class MaskItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes? = null
)

data class Recommendations(

	@field:SerializedName("basic_routine")
	val basicRoutine: BasicRoutine? = null,

	@field:SerializedName("notes")
	val notes: List<String?>? = null,

	@field:SerializedName("additional_care")
	val additionalCare: AdditionalCare? = null
)

data class TonerItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes? = null
)

data class MoisturizerItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes? = null
)

data class SerumItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes? = null
)

data class AdditionalCare(

	@field:SerializedName("acne_spot")
	val acneSpot: List<AcneSpotItem?>? = null,

	@field:SerializedName("eye_cream")
	val eyeCream: List<Any?>? = null,

	@field:SerializedName("mask")
	val mask: List<MaskItem?>? = null
)

data class SkinTypes(

	@field:SerializedName("normal")
	val normal: Boolean? = null,

	@field:SerializedName("oily")
	val oily: Boolean? = null,

	@field:SerializedName("dry")
	val dry: Boolean? = null
)

data class SunscreenItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes? = null
)

data class FacialWashItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes? = null
)

data class BasicRoutine(

	@field:SerializedName("moisturizer")
	val moisturizer: List<MoisturizerItem?>? = null,

	@field:SerializedName("sunscreen")
	val sunscreen: List<SunscreenItem?>? = null,

	@field:SerializedName("toner")
	val toner: List<TonerItem?>? = null,

	@field:SerializedName("serum")
	val serum: List<SerumItem?>? = null,

	@field:SerializedName("facial_wash")
	val facialWash: List<FacialWashItem?>? = null
)

data class AcneSpotItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("ingredients")
	val ingredients: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes? = null
)*/
