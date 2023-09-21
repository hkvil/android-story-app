package com.example.dicodingstoryapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.data.database.UserPreference
import com.example.dicodingstoryapp.data.database.dataStore
import com.example.dicodingstoryapp.paging.StoryListAdapter2
import com.example.dicodingstoryapp.data.viewmodel.HomeViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModelFactory
import com.example.dicodingstoryapp.databinding.ActivityHomeBinding
import com.example.dicodingstoryapp.paging.StoryRepository
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userViewModel: PreferencesViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Stories"
        setViewModel()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity();
        finish();
    }

    private fun setViewModel() {
        val pref = UserPreference.getInstance(application.dataStore)



        userViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[PreferencesViewModel::class.java]

        userViewModel.getUserToken().observe(this) {
          //  showProgressBar(true)
            lifecycleScope.launch {
                Log.d("T", it.toString())
//                homeViewModel.getStories(it) BEFORE PAGING
                token = it
                showRecyclerView()
            }
        }


    }

    private fun showRecyclerView() {
        homeViewModel = ViewModelProvider(
            this, HomeViewModel.HomeViewModelFactory(StoryRepository(token.toString()))
        )[HomeViewModel::class.java]
//        binding.rvStories.layoutManager = LinearLayoutManager(this)
//        binding.rvStories.adapter = StoryListAdapter(list)  BEFORE PAGING
        val adapter = StoryListAdapter2()
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter
        homeViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }



    }

    private fun logout() {
        userViewModel.deleteUserToken()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_story -> {
                val intent = Intent(this, AddStoryActivity::class.java)
                if (checkMediaPermission()) {
                    startActivity(intent)
                }
                Toast.makeText(this, "ADD STORY", Toast.LENGTH_LONG).show()
                true
            }

            R.id.logout -> {
                logout()
                Toast.makeText(this, "LOGOUT", Toast.LENGTH_LONG).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun checkMediaPermission(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }

            else -> {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
                return false
            }
        }
    }

    private fun showProgressBar(activated: Boolean) {
        if (activated) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


}