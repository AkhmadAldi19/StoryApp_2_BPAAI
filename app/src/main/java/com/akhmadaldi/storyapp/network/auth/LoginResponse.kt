package com.akhmadaldi.storyapp.network.auth

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)