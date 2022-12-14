package com.akhmadaldi.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akhmadaldi.storyapp.data.local.entity.StoryEntity
import com.akhmadaldi.storyapp.databinding.ActivityDetailStoryBinding
import com.akhmadaldi.storyapp.utils.DateFormatter
import com.bumptech.glide.Glide
import java.util.*

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<StoryEntity>("Story") as StoryEntity
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story.name
        binding.tvFirstLetterName.text = story.name.substring(0, 1)
        binding.tvDetailDescription.text = story.description
        binding.tvDetailCreated.text = DateFormatter.formatDate(story.createdAt, TimeZone.getDefault().id)
    }

}