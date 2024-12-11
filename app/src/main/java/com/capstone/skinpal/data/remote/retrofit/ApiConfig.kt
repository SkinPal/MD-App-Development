package com.capstone.skinpal.data.remote.retrofit

import com.capstone.skinpal.BuildConfig
import com.capstone.skinpal.BuildConfig.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        private fun createLoggingInterceptor() = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        private fun createAuthClient(token: String) = OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .apply {
                connectTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
            }
            .build()

        private fun createBasicClient() = OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .apply {
                connectTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
            }
            .build()

        private inline fun <reified T> createService(
            baseUrl: String,
            client: OkHttpClient
        ): T {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(T::class.java)
        }

        // API dengan auth
        fun getApiService(token: String): ApiService =
            createService(BuildConfig.BASE_URL, createAuthClient(token))

        // API tanpa auth
        fun getApiService2(): ApiService2 =
            createService(BuildConfig.BASE_URL_2, createBasicClient())
    }
}