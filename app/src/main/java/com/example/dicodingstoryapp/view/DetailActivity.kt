package com.example.dicodingstoryapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dicodingstoryapp.data.Story
import com.example.dicodingstoryapp.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var story: Story
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        story = intent.getParcelableExtra<Story>("data")!!
        setContentView(binding.root)
        lifecycleScope.launch {
            setData()
        }
    }

    private fun setData() {
        binding.apply {
            tvTitle.text = story.title
            tvDesc.text = story.desc
            Glide.with(this@DetailActivity).load(story.imageUrl).into(ivPhoto)
        }
    }
}