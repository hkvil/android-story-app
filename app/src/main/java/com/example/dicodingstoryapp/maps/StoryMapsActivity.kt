package com.example.dicodingstoryapp.maps

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.data.response.ListStoryItem
import com.example.dicodingstoryapp.databinding.ActivityStoryMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding
    private val storyMapsViewModel: StoryMapsViewModel by viewModels()
    private lateinit var token: String
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = intent.getStringExtra("data").toString()
        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            getStoryWithLoc()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun styleMap() {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
    }

    private suspend fun getStoryWithLoc() {
        Log.d("MAP", token)
        storyMapsViewModel.getStoryWithLoc(token)
        storyMapsViewModel.responseBody.observe(this) {
            if (it != null) {
                Log.d("MAP", it.listStory.size.toString())
                addStoryMarkers(it.listStory)
            }
        }
    }

    private fun addStoryMarkers(listStory: List<ListStoryItem>) {
        listStory.forEach {
            val latLng = LatLng(it.lat as Double, it.lon as Double)
            mMap.addMarker(
                MarkerOptions().title(it.name).snippet(it.description).position(latLng)
            )
            boundsBuilder.include(latLng)
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        styleMap()
    }
}