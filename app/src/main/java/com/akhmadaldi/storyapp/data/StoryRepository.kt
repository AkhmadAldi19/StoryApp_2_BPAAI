package com.akhmadaldi.storyapp.data

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.akhmadaldi.storyapp.data.local.entity.StoryEntity
import com.akhmadaldi.storyapp.data.local.room.StoryDatabase
import com.akhmadaldi.storyapp.network.ApiService
import com.akhmadaldi.storyapp.network.auth.LoginResponse
import com.akhmadaldi.storyapp.network.auth.RegisterResponse
import com.akhmadaldi.storyapp.network.auth.RegisterResult
import com.akhmadaldi.storyapp.network.story.FileUploadResponse
import com.akhmadaldi.storyapp.preference.UserPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    val loadRepo = MutableLiveData<Boolean>()

    fun getStory(token: String): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 2*PAGE_SIZE,
                initialLoadSize = 2*PAGE_SIZE
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoryLocation(token: String): LiveData<ResultResponse<List<StoryEntity>>> = liveData {
        loadRepo.value = true
        try {
            val responseData = apiService.getListStoriesLocation("Bearer $token",  1)
            val responseDataList = responseData.listStory
            val storyList = responseDataList.map { story ->
                StoryEntity(
                    story.id,
                    story.name,
                    story.description,
                    story.photoUrl,
                    story.createdAt,
                    story.lat,
                    story.lon
                )
            }
            emit(ResultResponse.Success(storyList))
            storyDatabase.storyDao().deleteAll()
            storyDatabase.storyDao().insertStory(storyList)
        } catch (e: Exception) {
            Log.d("StoryRepository", "getStoryLocation: ${e.message.toString()} ")
            emit(ResultResponse.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
        val localData: LiveData<ResultResponse<List<StoryEntity>>> = storyDatabase.storyDao().getAllStoryLocation().map { ResultResponse.Success(it) }
        emitSource(localData)
    }

    companion object {
        const val PAGE_SIZE = 3
    }

    fun login(email: String, password: String, pref: UserPreference? = null) : LiveData<ResultResponse<LoginResponse>> = liveData {
        loadRepo.value = true
        try {
            val response = apiService.loginUser(email, password)
            saveToken(response.loginResult.token ,pref)
            emit(ResultResponse.Success(response))
        } catch (e: Exception) {
            Log.d("StoryRepository", "login: ${e.message.toString()} ")
            emit(ResultResponse.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
    }

    fun register(user: RegisterResult) : LiveData<ResultResponse<RegisterResponse>> = liveData {
        loadRepo.value = true
        try {
            val response = apiService.registerUser(user.name, user.email, user.password)
            emit(ResultResponse.Success(response))
        } catch (e: Exception) {
            Log.d("StoryRepository", "register: ${e.message.toString()} ")
            emit(ResultResponse.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
    }

    fun uploadImage(file: MultipartBody.Part, description: RequestBody, token: String, lat: Double?, lon: Double?) : LiveData<ResultResponse<FileUploadResponse>> = liveData  {
        loadRepo.value = true
        try {
            val responseData = apiService.uploadImage(file, description, "Bearer $token",lat,lon)
            emit(ResultResponse.Success(responseData))
        } catch (e: Exception) {
            Log.d("StoryRepository", "uploadImage: ${e.message.toString()} ")
            emit(ResultResponse.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
    }


    private suspend fun saveToken(token: String, pref: UserPreference?){
        pref?.login(token)
    }

}
