package com.example.dicodingstoryapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.data.database.UserPreference
import com.example.dicodingstoryapp.data.database.dataStore
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModelFactory
import com.example.dicodingstoryapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: PreferencesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkIfLogged()
        setupIntent()
    }

    private fun checkIfLogged() {
        val pref = UserPreference.getInstance(application.dataStore)
        userViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[PreferencesViewModel::class.java]
        userViewModel.getUserToken().observe(this) {
            if ((it != null) && (it != "NULL")) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupIntent() {
        binding.apply {
            btLogin.setOnClickListener {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            btRegister.setOnClickListener {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }
}