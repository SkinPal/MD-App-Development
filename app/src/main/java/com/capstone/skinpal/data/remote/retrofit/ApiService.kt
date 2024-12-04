package com.capstone.skinpal.data.remote.retrofit

import com.capstone.skinpal.data.remote.response.ArticleResponse
import com.capstone.skinpal.data.remote.response.LoginResponse
import com.capstone.skinpal.data.remote.response.ProductResponse
import com.capstone.skinpal.data.remote.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    suspend fun getProduct(): ProductResponse
}
