package com.capstone.skinpal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.capstone.skinpal.BuildConfig
import com.capstone.skinpal.data.local.entity.ArticleEntity
import com.capstone.skinpal.data.local.room.ArticleDao
import com.capstone.skinpal.data.remote.retrofit.ApiService
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.data.local.room.ImageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class Repository(
    private val apiService: ApiService,
    private val articleDao: ArticleDao,
    private val imageDao: ImageDao
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

    suspend fun saveImage(image: ImageEntity) {
        withContext(Dispatchers.IO) {
            imageDao.insertItem(image)
        }
    }

    fun getPredictionItems(): LiveData<Result<List<ImageEntity>>> {
        return imageDao.getItem().asLiveData().map { items ->
            if (items.isNotEmpty()) Result.Success(items) else Result.Error("No saved items")
        }
    }


    suspend fun removePrediction(id: Int) {
        withContext(Dispatchers.IO) {
            imageDao.deleteItem(id)
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            articleDao: ArticleDao,
            imageDao: ImageDao
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, articleDao, imageDao)
            }.also { instance = it }
    }
}
