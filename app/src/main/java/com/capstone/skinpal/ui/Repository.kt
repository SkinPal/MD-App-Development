package com.capstone.skinpal.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.capstone.skinpal.data.local.room.ArticleDao
import com.capstone.skinpal.data.remote.retrofit.ApiService
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserModel
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.entity.ImageEntity
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.data.local.room.ImageDao
import com.capstone.skinpal.data.local.room.ProductDao
import com.capstone.skinpal.data.remote.response.LoginResponse
import com.capstone.skinpal.data.remote.retrofit.LoginRequest
import com.capstone.skinpal.data.remote.retrofit.RegisterRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class Repository(
    private val apiService: ApiService,
    private val articleDao: ArticleDao,
    private val productDao: ProductDao,
    private val userPreference: UserPreference,
    private val imageDao: ImageDao
) {

    fun getImage(week: Int): LiveData<Result<ImageEntity>> {
        return imageDao.getItem().asLiveData().map { items ->
            val filteredItem = items.find { it.week == week }
            filteredItem?.let {
                Result.Success(it)
            } ?: Result.Error("No saved item for week $week")
        }
    }

    fun getProduct(): LiveData<Result<List<ProductEntity>>> = liveData {
        emit(Result.Loading)
        try {
            // Fetch data from API
            val response = apiService.getProduct()
            Log.d("API Response", response.toString())

            // Map API response to local entity
            val productEntities = response.productResponse.map { productItem ->
                ProductEntity(
                    name = productItem.name,
                    imageUrl = productItem.imageUrl
                )
            }

            // Save mapped data into database
            productDao.insertProduct(productEntities)
            Log.d("DB Insert", "Inserted products: $productEntities")

        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch products: ${e.message}"))
            return@liveData
        }

        // Observe the database and emit as LiveData
        val localData: LiveData<List<ProductEntity>> = productDao.getProduct()
        emitSource(localData.map { Result.Success(it) })
    }


    /*fun registerUser(jsonBody: String): Response {
        val request = Request.Builder()
            .url("https://your-api-url.com/register")
            .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
            .addHeader("Content-Type", "application/json")
            .build()

        return client.newCall(request).execute()
    }*/


    fun register(name: String, user_id: String, email: String, password: String): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            withContext(Dispatchers.IO) {
                // Create RegisterRequest object
                val registerRequest = RegisterRequest(
                    name = name,
                    user_id = user_id,
                    email = email,
                    password = password
                )
                apiService.register(registerRequest)
            }
            emit(Result.Success("Registration successful"))
        } catch (e: HttpException) {
           // val errorMessage = e.response()?.errorBody()?.string()?.let {
             //   Gson().fromJson(it, FileUploadResponse::class.java).message
            //} ?: "Registration failed"
            //emit(Result.Error(errorMessage))
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message ?: "Unable to connect"}"))
        } catch (e: kotlin.Exception) {
            emit(Result.Error("Registration failed: ${e.message ?: "Unknown error"}"))
        }
    }

    fun login(userId: String, password: String): LiveData<Result<UserModel>> = liveData {
        emit(Result.Loading)
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.login(LoginRequest(userId, password))
            }

            val userModel = response.let {
                UserModel(
                    user = it.user.toString(),
                    token = it.accessToken.orEmpty(),
                    isLogin = true
                )
            }
            emit(Result.Success(userModel))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let {
                Gson().fromJson(it, LoginResponse::class.java).message
            } ?: "Login failed"
            emit(Result.Error(errorMessage))
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message ?: "Unable to connect"}"))
        } catch (e: Exception) {
            emit(Result.Error("Login failed: ${e.message ?: "Unknown error"}"))
        }
    }


    fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    /*fun getArticle(): LiveData<Result<List<ArticleEntity>>> = liveData {
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
    }*/

    suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    suspend fun saveImage(image: ImageEntity) {
        withContext(Dispatchers.IO) {
            imageDao.insertItem(image)
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
            productDao: ProductDao,
            userPreference: UserPreference,
            imageDao: ImageDao
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, articleDao, productDao, userPreference, imageDao)
            }.also { instance = it }
    }
}
