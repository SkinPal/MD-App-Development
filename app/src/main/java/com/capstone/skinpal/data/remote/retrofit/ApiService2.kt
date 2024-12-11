package com.capstone.skinpal.data.remote.retrofit

import com.capstone.skinpal.data.remote.response.ArticleResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService2 {
    @GET("everything?q=skincare&from=2024-12-10&to=2024-12-30&sortBy=relevant")
    suspend fun getArticle(@Query("apiKey") apiKey: String): ArticleResponse
}