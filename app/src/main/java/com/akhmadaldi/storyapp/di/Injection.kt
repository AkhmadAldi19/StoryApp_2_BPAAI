package com.akhmadaldi.storyapp.di

import android.content.Context
import com.akhmadaldi.storyapp.data.StoryRepository
import com.akhmadaldi.storyapp.data.local.room.StoryDatabase
import com.akhmadaldi.storyapp.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}