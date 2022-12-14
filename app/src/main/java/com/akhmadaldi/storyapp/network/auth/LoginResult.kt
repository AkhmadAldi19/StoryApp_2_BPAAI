package com.akhmadaldi.storyapp.network.auth

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)