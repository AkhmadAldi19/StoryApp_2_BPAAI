package com.akhmadaldi.storyapp.network.story


data class StoryLocationListResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<ListStoryLocationItem>
)