package com.akhmadaldi.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.akhmadaldi.storyapp.R
import com.akhmadaldi.storyapp.data.local.entity.StoryEntity
import com.akhmadaldi.storyapp.databinding.ItemlistStoryBinding
import com.akhmadaldi.storyapp.ui.detail.DetailStoryActivity
import com.akhmadaldi.storyapp.utils.DateFormatter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class StoryAdapter :
    PagingDataAdapter<StoryEntity, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    class ListViewHolder(binding: ItemlistStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private var photo = binding.ivItemPhoto
        private var profileName = binding.firstLetterName
        private var userName = binding.tvItemName
        private var time = binding.tvItemTime

        fun bind(story: StoryEntity) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.icon_park_loading_one).error(R.drawable.ic_round_broken_image))
                .into(photo)
            userName.text = story.name
            profileName.text = story.name.substring(0, 1)
            time.text = DateFormatter.formatTime(story.createdAt, TimeZone.getDefault().id)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("Story", story)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemBinding = ItemlistStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}