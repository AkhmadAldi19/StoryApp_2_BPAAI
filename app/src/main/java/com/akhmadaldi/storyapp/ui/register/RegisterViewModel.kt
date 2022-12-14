package com.akhmadaldi.storyapp.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.network.auth.RegisterResult

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    val loading : MutableLiveData<Boolean> = storyRepository.loadRepo

    fun registerRepo(user: RegisterResult) = storyRepository.register(user)
}