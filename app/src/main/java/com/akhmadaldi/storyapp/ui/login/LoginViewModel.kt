package com.akhmadaldi.storyapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.preference.UserPreference

class LoginViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {
    val loading : MutableLiveData<Boolean> = storyRepository.loadRepo

    fun loginRepo(email: String, password: String) = storyRepository.login(email, password, pref)
}