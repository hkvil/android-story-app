package com.example.dicodingstoryapp.paging

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.data.Story
import com.example.dicodingstoryapp.data.response.ListStoryItem
import com.example.dicodingstoryapp.view.DetailActivity


class StoryListAdapterPaging :
    PagingDataAdapter<ListStoryItem, StoryListAdapterPaging.StoryViewHolder>(DIFF_CALLBACK) {
    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_title)
        var desc: TextView = itemView.findViewById(R.id.tv_desc)
        var photo: ImageView = itemView.findViewById(R.id.iv_image)


        fun bind(data: ListStoryItem?) {
            if (data != null) {
                title.text = data.name
                desc.text = data.description
                Glide.with(itemView).load(data.photoUrl).into(photo)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }


    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
        val titleString = data?.name.toString()
        val descString = data?.description.toString()
        val urlString = data?.photoUrl.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("data", Story(titleString, descString, urlString))
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.title, "title"),
                    Pair(holder.desc, "desc"),
                    Pair(holder.photo, "photo"),
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }

    }
}