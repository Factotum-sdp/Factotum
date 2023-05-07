package com.github.factotum_sdp.factotum.ui.bossmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ZOOM_LEVEL_CITY = 14f
private const val WAIT_TIME_LOCATION_UPDATE = 30000L

class BossMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler(Looper.getMainLooper())
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requireContext().hasLocationPermission()
            activateLocation(googleMap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_boss_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance().getReference("Location")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Set custom map style to remove POIs
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style_without_pois
            )
        )

        if (!requireContext().hasLocationPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            activateLocation(googleMap)
        }

        // Start fetching data periodically
        handler.post(fetchDataRunnable)
    }

    private fun activateLocation(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLatLng,
                            ZOOM_LEVEL_CITY
                        )
                    )
                }
            }
        } else {
            // Default location
            val defaultLatLng = LatLng(0.0, 0.0)
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    defaultLatLng,
                    2f
                )
            )
        }
    }

    private val fetchDataRunnable = object : Runnable {
        override fun run() {
            fetchLocationsAndUpdateMarkers()
            handler.postDelayed(this, WAIT_TIME_LOCATION_UPDATE)
        }
    }

    private fun fetchLocationsAndUpdateMarkers() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                googleMap.clear()

                snapshot.children.forEach { child ->
                    val uid = child.key ?: "Unknown"
                    val name = child.child("name").getValue(String::class.java) ?: "Unknown"
                    val latitude = child.child("latitude").getValue(Double::class.java) ?: 0.0
                    val longitude = child.child("longitude").getValue(Double::class.java) ?: 0.0

                    val position = LatLng(latitude, longitude)
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(colorForUid(uid)))
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BossMapFragment: ", "onCancelled: $error")
            }
        })
    }

    private fun colorForUid(uid: String): Float {
        return (uid.hashCode() and 0x00FFFFFF).toFloat() % 360
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        handler.removeCallbacks(fetchDataRunnable)
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

