package com.capstone.skinpal.data.remote.retrofit

import com.capstone.skinpal.data.remote.response.ArticleResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything?q=skincare&from=2024-11-10&to=2024-11-17&sortBy=relevant")
    suspend fun getArticle(@Query("apiKey") apiKey: String): ArticleResponse
}
