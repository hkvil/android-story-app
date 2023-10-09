package com.example.dicodingstoryapp.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModel
import com.example.dicodingstoryapp.data.viewmodel.RegisterViewModel
import com.example.dicodingstoryapp.databinding.ActivityRegisterBinding
import com.example.dicodingstoryapp.view.component.AlertFragment
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var userViewModel: PreferencesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        setListener()
    }

    private fun setViewModel() {
        registerViewModel.responseBody.observe(this) {
            Log.d("OBSERVE", "INSIDE OBSERVE")
            if (it != null) {
                showProgressBar(false)
                AlertFragment("Bukan","Bukan").show(supportFragmentManager, "REGISTER")
            } else {
                showProgressBar(false)
                AlertFragment("ERROR","Error").show(supportFragmentManager, "REGISTER")
            }
        }
    }

    private fun setListener() {
        binding.button.setOnClickListener {
            val name = binding.textInputEditName.text.toString()
            val email = binding.textInputEditEmail.text.toString()
            val password = binding.textInputEditPassword.text.toString()
            lifecycleScope.launch {
                Log.d("VM_LOGIN", email + password)
                showProgressBar(true)
                registerViewModel.postRegister(name, email, password)
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