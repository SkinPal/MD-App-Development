package com.capstone.skinpal.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(

	@field:SerializedName("ProductResponse")
	val productResponse: List<ProductResponseItem>
)

data class ProductResponseItem(

	@field:SerializedName("skin_conditions")
	val skinConditions: SkinConditions,

	@field:SerializedName("image_url")
	val imageUrl: String,

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

data class SkinConditions(

	@field:SerializedName("wrinkles")
	val wrinkles: Boolean,

	@field:SerializedName("acne")
	val acne: Boolean,

	@field:SerializedName("redness")
	val redness: Boolean
)

data class SkinTypes(

	@field:SerializedName("normal")
	val normal: Boolean,

	@field:SerializedName("oily")
	val oily: Boolean,

	@field:SerializedName("dry")
	val dry: Boolean
)
