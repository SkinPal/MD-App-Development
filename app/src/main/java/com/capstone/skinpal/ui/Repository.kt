package com.capstone.skinpal.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import com.capstone.skinpal.data.local.room.AnalysisDatabase
import com.capstone.skinpal.data.local.room.ImageDao
import com.capstone.skinpal.data.local.room.ProductDao
import com.capstone.skinpal.data.local.room.SkinAnalysisDao
import com.capstone.skinpal.data.remote.response.ErrorResponse
import com.capstone.skinpal.data.remote.response.FileUploadResponse
import com.capstone.skinpal.data.remote.response.ProfileResponse
import com.capstone.skinpal.data.remote.response.UploadProfileResponse
import com.capstone.skinpal.data.remote.retrofit.LoginRequest
import com.capstone.skinpal.data.remote.retrofit.RegisterRequest
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
    private val articleDao: ArticleDao,
    private val productDao: ProductDao,
    private val userPreference: UserPreference,
    private val imageDao: ImageDao,
    private val skinAnalysisDao: SkinAnalysisDao,
    private val analysisDatabase: AnalysisDatabase
) {

    fun clearSession() {
        userPreference.logout()
    }

    /*fun getImage(week: String): LiveData<Result<ImageEntity>> {
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
    }*/

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
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                // Parse the specific error message from server
                Gson().fromJson(errorBody, ErrorResponse::class.java).detail
                    ?: "Register failed"
            } catch (jsonException: Exception) {
                Log.e("Repository", "Error parsing error response: ${jsonException.message}")
                "REgister failed"
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

    fun getAnalysisByUserId(userId: String): LiveData<Result<AnalysisEntity>> = liveData {
        emit(Result.Loading) // Emit a loading state

        try {
            val analysis = withContext(Dispatchers.IO) {
                skinAnalysisDao.getLatestAnalysisByUserId(userId) // Fetch from database
            }

            if (analysis != null) {
                emit(Result.Success(analysis)) // Emit the successful result
            } else {
                //emit(Result.Error("No analysis found for userId: $userId")) // Emit an error if no analysis is found
            }
        } catch (e: Exception) {
            emit(Result.Error("Error fetching analysis: ${e.message}")) // Emit an error if an exception occurs
        }
    }

    fun getResult(): LiveData<Result<AnalysisEntity>> = liveData {
        emit(Result.Loading) // Emit a loading state

        try {
            val analysis = withContext(Dispatchers.IO) {
                skinAnalysisDao.getResult() // Fetch from database
            }

            if (analysis != null) {
                emit(Result.Success(analysis)) // Emit the successful result
            } else {
                //emit(Result.Error("No analysis found for userId: $userId")) // Emit an error if no analysis is found
            }
        } catch (e: Exception) {
            emit(Result.Error("Error fetching analysis: ${e.message}")) // Emit an error if an exception occurs
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
            // Get user session
            val userSession = userPreference.getSession()
            val userId = userSession.user ?: throw Exception("User session is invalid")

            // Prepare image for upload
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestImageFile)

            // Make API call
            val response = apiService.analyzeImage(userId, week, imagePart).execute()

            if (response.isSuccessful) {
                response.body()?.let { result ->

                    // Map API response to model
                    val skinHealthData = result.resultYourSkinhealth

                    // Prepare AnalysisEntity to store in the database
                    val analysisEntity = AnalysisEntity(
                        userId = userId,
                        week = week,
                        imageUri = imageFile.absolutePath,
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
                        timestamp = System.currentTimeMillis()// Can adjust based on how you want to display recommendations
                    )

                    skinAnalysisDao.insertAnalysis(analysisEntity)

                    // Emit success with the result
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

    fun JsonElement.toPercent(): String {
        return if (this.isJsonPrimitive && this.asJsonPrimitive.isNumber) {
            String.format(Locale.US, "%.2f%%", this.asDouble * 100) // Specify the locale explicitly
        } else {
            "N/A" // Handle cases where the JsonElement is not a number
        }
    }

    suspend fun removePrediction(id: Int) {
        withContext(Dispatchers.IO) {
            imageDao.deleteItem(id)
        }
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
            articleDao: ArticleDao,
            productDao: ProductDao,
            userPreference: UserPreference,
            imageDao: ImageDao,
            skinAnalysisDao: SkinAnalysisDao,
            analysisDatabase: AnalysisDatabase
        ): Repository = Repository(apiService, articleDao, productDao, userPreference, imageDao, skinAnalysisDao, analysisDatabase)
    }
}
