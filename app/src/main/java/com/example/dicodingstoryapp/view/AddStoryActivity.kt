package com.example.dicodingstoryapp.view

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.data.database.UserPreference
import com.example.dicodingstoryapp.data.database.dataStore
import com.example.dicodingstoryapp.data.helper.getImageUri
import com.example.dicodingstoryapp.data.viewmodel.AddStoryViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModelFactory
import com.example.dicodingstoryapp.databinding.ActivityAddStoryBinding
import com.example.dicodingstoryapp.data.helper.uriToFile
import com.example.dicodingstoryapp.view.component.AlertFragment
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var userViewModel: PreferencesViewModel
    private val addStoryViewModel: AddStoryViewModel by viewModels()
    private var file: File? = null
    private var token: String? = null
    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentImageUri = getImageUri(this)
        setListener()
        setViewModel()
    }

    private fun setViewModel() {
        val pref = UserPreference.getInstance(application.dataStore)
        userViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[PreferencesViewModel::class.java]
        userViewModel.getUserToken().observe(this) {
            token = it
        }
        addStoryViewModel.responseBody.observe(this) {
            showProgressBar(false)
            if (it?.error == false) {
                finish()
            } else {
                AlertFragment(it?.message.toString()).show(supportFragmentManager, "ADD")
            }

        }
    }

    private fun setListener() {
        binding.apply {
            btCamera.setOnClickListener {
                onClickCamera()
            }
            btGallery.setOnClickListener {
                onClickGallery()
            }
            btUpload.setOnClickListener {
                onClickUpload()
            }
        }
    }

    private fun onClickCamera() {
//        val REQUEST_IMAGE_CAPTURE = 1
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        startCamera()
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
            file = uriToFile(currentImageUri!!,this)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPhoto.setImageURI(it)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        showProgressBar(false)
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            Log.d("CAMERA", data?.data.toString())
//            if (data != null) {
//                val fullPhotoUri: Uri? = data.data
//                if (fullPhotoUri != null) {
//                    binding.ivPhoto.setImageURI(fullPhotoUri)
//                    file = uriToFile(fullPhotoUri, this)
//                }
//            }
//        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Log.d("GALLERY", data.toString())
            if (data != null) {
                val fullPhotoUri: Uri? = data.data
                if (fullPhotoUri != null) {
                    binding.ivPhoto.setImageURI(fullPhotoUri)
                    file = uriToFile(fullPhotoUri, this)
                }
            }
        }
    }


    private fun onClickGallery() {
        val REQUEST_IMAGE_GET = 2
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_GET)
    }

    private fun onClickUpload() {
        if (token == null || binding.textInputEditDesc.text.isNullOrBlank() || file == null) {
            AlertFragment("ERROR").show(supportFragmentManager, "ADD")
        } else {
            showProgressBar(true)
            addStoryViewModel.uploadStory(
                token!!,
                binding.textInputEditDesc.text.toString(),
                file!!
            )
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