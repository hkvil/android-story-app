package com.example.dicodingstoryapp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.data.database.UserPreference
import com.example.dicodingstoryapp.data.database.dataStore
import com.example.dicodingstoryapp.data.helper.getImageUri
import com.example.dicodingstoryapp.data.helper.uriToFile
import com.example.dicodingstoryapp.data.viewmodel.AddStoryViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModel
import com.example.dicodingstoryapp.data.viewmodel.PreferencesViewModelFactory
import com.example.dicodingstoryapp.databinding.ActivityAddStoryBinding
import com.example.dicodingstoryapp.view.component.AlertFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var userViewModel: PreferencesViewModel
    private val addStoryViewModel: AddStoryViewModel by viewModels()
    private var file: File? = null
    private var token: String? = null
    private var currentImageUri: Uri? = null
    private var photoLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentImageUri = getImageUri(this)
        setListener()
        setViewModel()
        setLocationRequest()
    }

    private fun setLocationRequest() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            500L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                Log.d("LOCATION CALLBACK", p0.lastLocation.toString())
                photoLocation = p0.lastLocation
            }
        }
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
                finishAffinity()
                val intent = Intent(this, HomeActivity::class.java)
                stopLocationRequest()
                startActivity(intent)
            } else {
                AlertFragment("ERROR", "INI ERROR").show(supportFragmentManager, "ADD")
            }

        }
    }

    private fun stopLocationRequest() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
            btLocation.setOnClickListener {
                onActivatedCheckbox()
            }
        }
    }

    private fun onActivatedCheckbox() {
        if (binding.btLocation.isChecked) {
            getMyLastLocation()
        } else {
            stopLocationRequest()
        }
    }

    private fun onClickCamera() {
        startCamera()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun getMyLastLocation() {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
            file = uriToFile(currentImageUri!!, this)
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
        Log.d("ADDSTORY", photoLocation.toString())
        if (token == null || binding.textInputEditDesc.text.isNullOrBlank() || file == null) {
            AlertFragment(
                "ERROR", """
                token:$token
                desc:${binding.textInputEditDesc.text}
                file:$file
            """.trimIndent()
            ).show(supportFragmentManager, "ADD")
        } else if (binding.btLocation.isChecked && photoLocation == null) {
            AlertFragment("ERROR", "Location checked but gps turned off").show(
                supportFragmentManager,
                "ERROR"
            )
        } else {
            showProgressBar(true)
            addStoryViewModel.uploadStory(
                token!!,
                binding.textInputEditDesc.text.toString(),
                file!!,
                photoLocation
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
//fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    Log.d("LOCATION",location.toString())
//                    photoLocation = location
//                    Log.d("LOCATION PHOTO",photoLocation.toString())
//                } else {
//                    Toast.makeText(
//                        this@AddStoryActivity,
//                        "Location is not found. Turn on GPS",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    binding.btLocation.isChecked = false
//                }
//            }