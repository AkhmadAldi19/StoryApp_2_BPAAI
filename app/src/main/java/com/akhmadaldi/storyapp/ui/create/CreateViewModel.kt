package com.akhmadaldi.storyapp.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.preference.UserModel
import com.akhmadaldi.storyapp.preference.UserPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {
    val loading : MutableLiveData<Boolean> = storyRepository.loadRepo

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun uploadImageRepo(file: MultipartBody.Part, description: RequestBody, token: String, lat: Double?, lon: Double?) =
        storyRepository.uploadImage(file, description, token,lat,lon)

}