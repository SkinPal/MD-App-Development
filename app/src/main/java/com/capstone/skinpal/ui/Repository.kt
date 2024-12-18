package com.capstone.skinpal.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.capstone.skinpal.BuildConfig
import com.capstone.skinpal.data.local.room.ArticleDao
import com.capstone.skinpal.data.remote.retrofit.ApiService
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserModel
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.data.local.entity.ArticleEntity
import com.capstone.skinpal.data.local.room.AnalysisDatabase
import com.capstone.skinpal.data.local.room.ImageDao
import com.capstone.skinpal.data.local.room.ProductDao
import com.capstone.skinpal.data.local.room.SkinAnalysisDao
import com.capstone.skinpal.data.remote.response.ErrorResponse
import com.capstone.skinpal.data.remote.response.ProfileResponse
import com.capstone.skinpal.data.remote.response.UploadProfileResponse
import com.capstone.skinpal.data.remote.retrofit.ApiService2
import com.capstone.skinpal.data.remote.retrofit.LoginRequest
import com.capstone.skinpal.data.remote.retrofit.RegisterRequest
import com.capstone.skinpal.ui.history.AnalysisResult
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.Locale

class Repository(
    private val apiService: ApiService,
    private val apiService2: ApiService2,
    private val articleDao: ArticleDao,
    private val productDao: ProductDao,
    private val userPreference: UserPreference,
    private val skinAnalysisDao: SkinAnalysisDao
) {

    fun clearSession() {
        userPreference.logout()
    }

    fun getImage(userId: String, week: String): LiveData<Result<AnalysisEntity>> {
        return skinAnalysisDao.getImageByUserIdAndWeek(userId, week).map { analysisImageUri ->
            if (analysisImageUri != null) {
                Result.Success(analysisImageUri)
            } else {
                Result.Error("No image found for userId: $userId and week: $week")
            }
        }
    }

    fun searchProducts(query: String): LiveData<Result<List<ProductEntity>>> {
        val result = MediatorLiveData<Result<List<ProductEntity>>>()
        result.value = Result.Loading

        val searchResults = productDao.searchProducts("%$query%")

        result.addSource(searchResults) { products ->
            result.value = if (products.isNotEmpty()) Result.Success(products) else Result.Error("No products found")
        }

        return result
    }

    fun getProducts(): LiveData<Result<List<ProductEntity>>> = liveData {
        emit(Result.Loading)
        try {
            emitSource(productDao.getProduct().map { Result.Success(it) })

            val response = apiService.getProduct()

            val productEntities = response.map { product ->
                val isBookmarked = productDao.isProductBookmarked(product.name)
                ProductEntity(
                    name = product.name,
                    imageUrl = product.imageUrl,
                    description = product.description,
                    ingredients = product.ingredients,
                    type = product.type,
                    isBookmarked
                )
            }

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
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).detail
                    ?: "Register failed"
            } catch (jsonException: Exception) {
                Log.e("Repository", "Error parsing error response: ${jsonException.message}")
                "Register failed"
            }
            emit(Result.Error(errorMessage))
        } catch (e: IOException) {
            emit(Result.Error("Network error: Please check your internet connection"))
        } catch (e: Exception) {
            emit(Result.Error("Register failed: ${e.message ?: "Unknown error"}"))
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

    fun getAnalysisByUserId(userId: String): LiveData<Result<AnalysisEntity>> = liveData {
        emit(Result.Loading)

        try {
            val analysis = withContext(Dispatchers.IO) {
                skinAnalysisDao.getLatestAnalysisByUserId(userId) // Fetch from database
            }

            if (analysis != null) {
                emit(Result.Success(analysis))
            } else {
            }
        } catch (e: Exception) {
            emit(Result.Error("Error fetching analysis: ${e.message}")) // Emit an error if an exception occurs
        }
    }

    fun getAnalysis(user_id: String, week: String) = liveData(Dispatchers.IO) {
        try {
            val userSession = userPreference.getSession()
            val userId = userSession.user ?: throw Exception("User session is invalid")
            val response = apiService.getAnalysis(userId, week).execute()

            if (response.isSuccessful) {
                response.body()?.let { result ->

                    val skinHealthData = result.resultYourSkinhealth

                    val analysisResult = AnalysisResult(
                        userId = userId,
                        week = week,
                        imageUri = result.publicUrl,
                        skinType = skinHealthData.skinType,
                        acne = skinHealthData.skinConditions.acne.toPercent(),
                        redness = skinHealthData.skinConditions.redness.toPercent(),
                        wrinkles = skinHealthData.skinConditions.wrinkles.toPercent(),
                        recommendations = result.recommendations.toString(),
                        moisturizer = result.recommendations.moisturizer,
                        treatment = result.recommendations.treatment,
                        sunscreen = result.recommendations.sunscreen,
                        toner = result.recommendations.toner,
                        serum = result.recommendations.serum,
                        facialWash = result.recommendations.facialWash,
                        timestamp = System.currentTimeMillis(),
                        publicUrl = result.publicUrl,
                        percentage = result.progress.percentage?.toPercent(),
                        message = result.progress.message
                    )
                    emit(Result.Success(analysisResult))

                } ?: emit(Result.Error("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Result.Error("Analysis failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
        }
    }

    fun getResult(): LiveData<Result<AnalysisEntity>> = liveData {
        emit(Result.Loading)
        try {
            val analysis = withContext(Dispatchers.IO) {
                skinAnalysisDao.getResult() // Fetch from database
            }

            if (analysis != null) {
                emit(Result.Success(analysis))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error fetching analysis: ${e.message}")) // Emit an error if an exception occurs
        }
    }

    fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getArticle(): LiveData<Result<List<ArticleEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService2.getArticle(BuildConfig.API_KEY)
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

    fun analyzeImage(user_id: String, imageFile: File, week: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try {
            val userSession = userPreference.getSession()
            val userId = userSession.user ?: throw Exception("User session is invalid")

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestImageFile)

            val response = apiService.analyzeImage(userId, week, imagePart).execute()

            if (response.isSuccessful) {
                response.body()?.let { result ->

                    // Map API response to model
                    val skinHealthData = result.resultYourSkinhealth

                    // Prepare AnalysisEntity to store in the database
                    val analysisResult = AnalysisResult(
                        userId = userId,
                        week = week,
                        imageUri = result.publicUrl,
                        skinType = skinHealthData.skinType,
                        acne = skinHealthData.skinConditions.acne.toPercent(),
                        redness = skinHealthData.skinConditions.redness.toPercent(),
                        wrinkles = skinHealthData.skinConditions.wrinkles.toPercent(),
                        recommendations = result.recommendations.toString(),
                        moisturizer = result.recommendations.moisturizer,
                        treatment = result.recommendations.treatment,
                        sunscreen = result.recommendations.sunscreen,
                        toner = result.recommendations.toner,
                        serum = result.recommendations.serum,
                        facialWash = result.recommendations.facialWash,
                        timestamp = System.currentTimeMillis(),
                        publicUrl = result.publicUrl,
                        percentage = result.progress.percentage?.toPercent(),
                        message = result.progress.message
                    )
                    emit(Result.Success(analysisResult))

                } ?: emit(Result.Error("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Result.Error("Analysis failed: ${response.code()} - $errorBody"))
            }

        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun JsonElement.toPercent(): String {
        return if (this.isJsonPrimitive && this.asJsonPrimitive.isNumber) {
            String.format(Locale.US, "%.2f%%", this.asDouble )
        } else {
            "N/A"
        }
    }

    fun getDetailProduct(name : String): LiveData<Result<ProductEntity>> = liveData {
        emit(Result.Loading)
        try {
            val eventDetail = apiService.getProductDetail()
            eventDetail.let { product ->
                val isBookmarked = productDao.isProductBookmarked(product.name)
                val productEntity = ProductEntity(
                    product.name,
                    product.imageUrl,
                    product.description,
                    product.ingredients,
                    product.type,
                    isBookmarked
                )
                productDao.insertProduct(listOf(productEntity))
            }
        }
        catch (e: kotlin.Exception) {
            emit(Result.Error(e.message.toString()))
        }
        val localData = productDao.getProductsByName(name).map { eventEntity ->
            Result.Success(eventEntity) as Result<ProductEntity>
        }
        emitSource(localData)
    }

    suspend fun setBookmarkedProduct(product: ProductEntity, bookmarkState: Boolean) {
        withContext(Dispatchers.IO) {
            product.isBookmarked = bookmarkState
            productDao.updateProduct(product)
        }
    }

    fun getFavoriteProduct(): LiveData<Result<List<ProductEntity>>> {
        val result = MediatorLiveData<Result<List<ProductEntity>>>()
        result.value = Result.Loading
        val localData = productDao.getBookmarkedProduct()
        result.addSource(localData) { events ->
            if (events.isNotEmpty()) {
                result.value = Result.Success(events)
            } else {
                result.value = Result.Error("No favorite products found")
            }
        }

        return result
    }

    suspend fun getUserProfile(userId: String): ProfileResponse {
        return apiService.getUserProfile(userId)
    }

    fun getUserSession(): UserModel {
        return userPreference.getSession()
    }

    suspend fun uploadProfileImage(image: MultipartBody.Part, userId: String): UploadProfileResponse {
        return apiService.uploadProfileImage(userId, image)
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            apiService2: ApiService2,
            articleDao: ArticleDao,
            productDao: ProductDao,
            userPreference: UserPreference,
            imageDao: ImageDao,
            skinAnalysisDao: SkinAnalysisDao,
            analysisDatabase: AnalysisDatabase
        ): Repository = Repository(apiService, apiService2, articleDao, productDao, userPreference,
            skinAnalysisDao
        )
    }
}
