package com.capstone.skinpal.data.remote.retrofit

import com.capstone.skinpal.data.remote.response.AnalyzeResponse
import com.capstone.skinpal.data.remote.response.ArticleResponse
import com.capstone.skinpal.data.remote.response.LoginResponse
import com.capstone.skinpal.data.remote.response.ProductResponse
import com.capstone.skinpal.data.remote.response.ProductResponseItem
import com.capstone.skinpal.data.remote.response.RegisterResponse
import com.capstone.skinpal.data.remote.response.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("users/")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    @POST("users/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @GET("everything?q=skincare&from=2024-11-10&to=2024-11-30&sortBy=relevant")
    suspend fun getArticle(@Query("apiKey") apiKey: String): ArticleResponse

    @GET("skincare")
    suspend fun getProduct(): List<ProductResponseItem>

    @Multipart
    @POST("users/upload")
    fun uploadImage(
        @Query("user_id") user_id: String,
        @Query("week") week: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @Multipart
    @POST("users/analyze")
    fun analyzeImage(
        @Query("user_id") user_id: String,
        @Query("week") week: String,
        @Part file: MultipartBody.Part
    ): Call<AnalyzeResponse>

}
