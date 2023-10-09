package com.example.dicodingstoryapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstoryapp.data.database.UserPreference
import com.example.dicodingstoryapp.data.database.dataStore
import com.example.dicodingstoryapp.data.viewmodel.LoginViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModelFactory
import com.example.dicodingstoryapp.databinding.ActivityLoginBinding
import com.example.dicodingstoryapp.view.component.AlertFragment
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var userViewModel: PreferencesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        setListener()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        finish()
    }

    private fun showAlertDialog(title:String, msg:String){
        AlertFragment(title,msg).show(supportFragmentManager, "LOGIN")
    }
    private fun setViewModel() {

        val pref = UserPreference.getInstance(application.dataStore)
        userViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[PreferencesViewModel::class.java]

        loginViewModel.responseBody.observe(this) {
            Log.d("OBSERVE", "INSIDE OBSERVE")
            if (it == null) {
                showProgressBar(false)
                showAlertDialog("NULL","Response is null")
            }
            if (it != null) {
                showProgressBar(false)
                val token = it.loginResult?.token
                userViewModel.saveUserToken(token.toString())
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun setListener() {
        binding.button.setOnClickListener {
            val email = binding.textInputEditEmail.text.toString()
            val password = binding.textInputEditPassword.text.toString()
            lifecycleScope.launch {
                Log.d("VM_LOGIN", email + password)
                showProgressBar(true)
                loginViewModel.postLogin(email, password)
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