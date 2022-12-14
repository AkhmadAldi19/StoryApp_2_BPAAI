package com.akhmadaldi.storyapp.network.story

import androidx.room.PrimaryKey

data class StoryListItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: String? = null,
    val lon: String? = null
)
