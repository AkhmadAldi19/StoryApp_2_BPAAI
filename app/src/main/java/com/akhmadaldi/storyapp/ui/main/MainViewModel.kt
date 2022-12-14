package com.akhmadaldi.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.data.local.entity.StoryEntity
import com.akhmadaldi.storyapp.preference.UserModel
import com.akhmadaldi.storyapp.preference.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {

    fun getToken(): LiveData<UserModel> {
        return pref.getToken().asLiveData()
    }

    fun story(token: String): LiveData<PagingData<StoryEntity>> {
        return storyRepository.getStory(token).cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}