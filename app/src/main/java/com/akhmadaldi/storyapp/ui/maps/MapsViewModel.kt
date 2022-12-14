package com.akhmadaldi.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.preference.UserModel
import com.akhmadaldi.storyapp.preference.UserPreference

class MapsViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun getStory(token: String) = storyRepository.getStoryLocation(token)

}