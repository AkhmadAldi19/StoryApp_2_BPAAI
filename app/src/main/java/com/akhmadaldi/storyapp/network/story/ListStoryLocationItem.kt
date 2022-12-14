package com.akhmadaldi.storyapp.network.story

import androidx.room.PrimaryKey

data class ListStoryLocationItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: String,
    val lon: String
)