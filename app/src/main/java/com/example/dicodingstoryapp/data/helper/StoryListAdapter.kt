package com.example.dicodingstoryapp.data.helper

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.data.Story
import com.example.dicodingstoryapp.data.response.ListStoryItem
import com.example.dicodingstoryapp.view.DetailActivity

class StoryListAdapter(private val list: List<ListStoryItem?>) :
    RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {
    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_title)
        var desc: TextView = itemView.findViewById(R.id.tv_desc)
        var photo: ImageView = itemView.findViewById(R.id.iv_image)

        fun bind(list: ListStoryItem?) {
            if (list != null) {
                title.text = list.name
                desc.text = list.description
                Glide.with(itemView).load(list.photoUrl).into(photo)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val titleString = list[position]?.name.toString()
        val descString = list[position]?.description.toString()
        val urlString = list[position]?.photoUrl.toString()
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("data", Story(titleString, descString, urlString))
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.title,"title"),
                    Pair(holder.desc,"desc"),
                    Pair(holder.photo,"photo"),
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

}