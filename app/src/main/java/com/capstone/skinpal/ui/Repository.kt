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
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.local.room.ImageDao
import com.capstone.skinpal.data.local.room.ProductDao
import com.capstone.skinpal.data.local.room.SkinAnalysisDao
import com.capstone.skinpal.data.remote.response.ErrorResponse
import com.capstone.skinpal.data.remote.response.FileUploadResponse
import com.capstone.skinpal.data.remote.retrofit.LoginRequest
import com.capstone.skinpal.data.remote.retrofit.RegisterRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.lang.Exception

class Repository(
    private val apiService: ApiService,
    private val articleDao: ArticleDao,
    private val productDao: ProductDao,
    private val userPreference: UserPreference,
    private val imageDao: ImageDao,
    private val skinAnalysisDao: SkinAnalysisDao
) {

    fun clearSession() {
        userPreference.logout()
    }

    fun getImage(week: String): LiveData<Result<ImageEntity>> {
        return imageDao.getItem().asLiveData().map { items ->
            val weekInt = week.toIntOrNull()
            if (weekInt == null) {
                Result.Error("Invalid week format: $week")
            } else {
                val filteredItem = items.find { it.week == weekInt }
                filteredItem?.let {
                    Result.Success(it)
                } ?: Result.Error("No saved item for week $week")
            }
        }
    }


    fun getProducts(): LiveData<Result<List<ProductEntity>>> = liveData {
        emit(Result.Loading)
        try {
            // Emit database content first
            emitSource(productDao.getProduct().map { Result.Success(it) })

            // Fetch from API
            val response = apiService.getProduct()

            // Map response items to entities
            val productEntities = response.map { item ->
                ProductEntity(
                    name = item.name,
                    imageUrl = item.imageUrl
                )
            }

            // Save to database
            withContext(Dispatchers.IO) {
                productDao.insertProduct(productEntities)
            }

        } catch (e: Exception) {
            Log.e("Repository", "Error refreshing products: ${e.message}", e)
            emit(Result.Error("Failed to load products: ${e.message}"))
        }
    }

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

    fun login(user_id: String, password: String): LiveData<Result<UserModel>> = liveData {
        emit(Result.Loading)
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.login(LoginRequest(user_id, password))
            }

            val userModel = response.let {
                UserModel(
                    user = response.user.userId,
                    token = response.accessToken,
                    isLogin = true
                )
            }
            emit(Result.Success(userModel))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                // Parse the specific error message from server
                Gson().fromJson(errorBody, ErrorResponse::class.java).detail
                    ?: "Login failed"
            } catch (jsonException: Exception) {
                Log.e("Repository", "Error parsing error response: ${jsonException.message}")
                "Login failed"
            }
            emit(Result.Error(errorMessage))
        } catch (e: IOException) {
            emit(Result.Error("Network error: Please check your internet connection"))
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

    fun uploadImage(
        user_id: String,
        imageFile: File,
        week: String
    ) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            // Get user session data from UserPreference
            val userSession = userPreference.getSession()
            val user_id = userSession.user // Make sure this returns just the userId string

            // Create multipart request
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                requestImageFile
            )

            // Make API call
            val response = apiService.uploadImage(
                user_id = userPreference.getSession().user.toString(),
                week = week,
                file = imagePart
            ).execute()

            if (response.isSuccessful) {
                emit(Result.Success(FileUploadResponse(false, "Image uploaded successfully")))
            } else {
               /* val errorBody = response.errorBody()?.string()
                Log.e("Repository", """
                Upload failed:
                Code: ${response.code()}
                Error: $errorBody
             """.trimIndent())
                emit(Result.Error("Upload failed: ${response.code()} - $errorBody"))*/
            }
        } catch (e: Exception) {
            Log.e("Repository", "Upload exception", e)
            //emit(Result.Error("Error: ${e.message}"))
        }
    }

    suspend fun insertSkinAnalysis(skinAnalysis: AnalysisEntity) {
        skinAnalysisDao.insertAnalysis(skinAnalysis)
    }

    fun analyzeImage(user_id: String, imageFile: File, week: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val userSession = userPreference.getSession()
            val userId = userSession.user ?: throw Exception("User session is invalid")

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart =
                MultipartBody.Part.createFormData("file", imageFile.name, requestImageFile)

            val response = apiService.analyzeImage(userId, week, imagePart).execute()

            if (response.isSuccessful) {
                response.body()?.let { result ->
                    val analysisEntity = AnalysisEntity(
                        skinType = result.data.analysis.resultYourSkinhealth.skinType,
                        skinConditions = result.data.analysis.resultYourSkinhealth.skinConditions.toString(),
                        recommendations = result.data.recommendations.toString(),
                    )
                    // Insert entity into Room database
                    skinAnalysisDao.insertAnalysis(analysisEntity)
                    emit(Result.Success(result))
                } ?: emit(Result.Error("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Result.Error("Analysis failed: ${response.code()} - $errorBody"))
            }

        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    suspend fun removePrediction(id: Int) {
        withContext(Dispatchers.IO) {
            imageDao.deleteItem(id)
        }
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            articleDao: ArticleDao,
            productDao: ProductDao,
            userPreference: UserPreference,
            imageDao: ImageDao,
            skinAnalysisDao: SkinAnalysisDao
        ): Repository = Repository(apiService, articleDao, productDao, userPreference, imageDao, skinAnalysisDao)
    }
}
