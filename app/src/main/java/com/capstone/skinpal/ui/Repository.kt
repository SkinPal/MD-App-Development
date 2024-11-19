package com.capstone.skinpal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.capstone.skinpal.BuildConfig
import com.capstone.skinpal.data.local.entity.ArticleEntity
import com.capstone.skinpal.data.local.room.ArticleDao
import com.capstone.skinpal.data.remote.retrofit.ApiService
import com.capstone.skinpal.data.Result
import java.lang.Exception

class Repository(
    private val apiService: ApiService,
    private val articleDao: ArticleDao
) {

    fun getArticle(): LiveData<Result<List<ArticleEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getArticle(BuildConfig.API_KEY)
            val articles = response.articles.map { article ->
                ArticleEntity(
                    title = article?.title ?: "Unknown Title",
                    urlToImage = article?.urlToImage ?: "",
                    url = article?.url ?: ""
                )
            }

            articleDao.insertArticle(articles)
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch articles: ${e.message}"))
        }

        val localData: LiveData<Result<List<ArticleEntity>>> =
            articleDao.getArticle().map { Result.Success(it) }
        emitSource(localData)
    }

    suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            articleDao: ArticleDao
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, articleDao)
            }.also { instance = it }
    }
}
