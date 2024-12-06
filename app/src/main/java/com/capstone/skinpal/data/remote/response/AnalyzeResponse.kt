package com.capstone.skinpal.data.remote.response

import com.google.gson.annotations.SerializedName

data class AnalyzeResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class Analysis(

	@field:SerializedName("skin_conditions_details")
	val skinConditionsDetails: SkinConditionsDetails,

	@field:SerializedName("skin_type_details")
	val skinTypeDetails: SkinTypeDetails,

	@field:SerializedName("result_your_skinhealth")
	val resultYourSkinhealth: ResultYourSkinhealth
)

data class Recommendations(

	@field:SerializedName("basic_routine")
	val basicRoutine: BasicRoutine,

	@field:SerializedName("notes")
	val notes: List<String>,

	@field:SerializedName("additional_care")
	val additionalCare: AdditionalCare
)

data class MoisturizerItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("ingredients")
	val ingredients: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes
)

data class MaskItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("ingredients")
	val ingredients: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes
)

data class SunscreenItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("ingredients")
	val ingredients: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes
)

data class FacialWashItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("ingredients")
	val ingredients: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes
)

data class SkinConditionsDetails(

	@field:SerializedName("wrinkles")
	val wrinkles: Any,

	@field:SerializedName("normal")
	val normal: Any,

	@field:SerializedName("acne")
	val acne: Any,

	@field:SerializedName("redness")
	val redness: Any
)

data class AdditionalCare(

	@field:SerializedName("acne_spot")
	val acneSpot: List<AcneSpotItem>,

	@field:SerializedName("eye_cream")
	val eyeCream: List<Any>,

	@field:SerializedName("mask")
	val mask: List<MaskItem>
)

data class SerumItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("ingredients")
	val ingredients: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes
)

data class Data(

	@field:SerializedName("public_url")
	val publicUrl: String,

	@field:SerializedName("analysis")
	val analysis: Analysis,

	@field:SerializedName("recommendations")
	val recommendations: Recommendations
)

data class AcneSpotItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("ingredients")
	val ingredients: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes
)

data class TonerItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("ingredients")
	val ingredients: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("skin_types")
	val skinTypes: SkinTypes
)

data class SkinTypeDetails(

	@field:SerializedName("normal")
	val normal: Any,

	@field:SerializedName("oily")
	val oily: Any,

	@field:SerializedName("dry")
	val dry: Any
)

data class BasicRoutine(

	@field:SerializedName("moisturizer")
	val moisturizer: List<MoisturizerItem>,

	@field:SerializedName("sunscreen")
	val sunscreen: List<SunscreenItem>,

	@field:SerializedName("toner")
	val toner: List<TonerItem>,

	@field:SerializedName("serum")
	val serum: List<SerumItem>,

	@field:SerializedName("facial_wash")
	val facialWash: List<FacialWashItem>
)

/*data class SkinConditions(

	@field:SerializedName("wrinkles")
	val wrinkles: Any,

	@field:SerializedName("normal")
	val normal: Any,

	@field:SerializedName("acne")
	val acne: Any,

	@field:SerializedName("redness")
	val redness: Any
)*/

data class ResultYourSkinhealth(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("skin_type")
	val skinType: String
)

/*data class SkinTypes(

	@field:SerializedName("normal")
	val normal: Boolean,

	@field:SerializedName("oily")
	val oily: Boolean,

	@field:SerializedName("dry")
	val dry: Boolean
)*/
