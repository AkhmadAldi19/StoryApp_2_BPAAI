package com.akhmadaldi.storyapp

import com.akhmadaldi.storyapp.data.local.entity.StoryEntity
import com.akhmadaldi.storyapp.network.auth.LoginResponse
import com.akhmadaldi.storyapp.network.auth.LoginResult
import com.akhmadaldi.storyapp.network.auth.RegisterResponse
import com.akhmadaldi.storyapp.network.story.FileUploadResponse
import com.akhmadaldi.storyapp.preference.UserModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object DummyData {
    fun generateDummyFileUploadSuccess(): FileUploadResponse {
        return FileUploadResponse(false, "Story created successfully")
    }

    fun generateDummyFileUploadError(): FileUploadResponse {
        return FileUploadResponse(true, "Failed to upload")
    }

    fun generateDummyUserDataLogin(): UserModel {
        return UserModel(
            "token",
            true,
        )
    }

    fun multipartFile() = MultipartBody.Part.create("dummyFile".toRequestBody())
    fun description () = "lorem".toRequestBody("text/plain".toMediaTypeOrNull())

    fun generateDummyLoginResponse(): LoginResponse {
        val itemLogin = LoginResult("1", "gudel", "token")
        return LoginResponse(false, "success", itemLogin)
    }

    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyList: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..10) {
            val story = StoryEntity(
                "$i",
                "Akhmad aldi",
                "Lorem Ipsum",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                "-6.9600243",
                "109.1406238",
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(false, "User created")
    }
}

