package com.github.factotum_sdp.factotum.ui.bossmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.hasLocationPermission
import com.github.factotum_sdp.factotum.models.CourierLocation
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

private const val ZOOM_LEVEL_CITY = 14f

class BossMapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: BossMapViewModel by viewModels()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

        viewModel.courierLocations.observe(viewLifecycleOwner) { locations ->
            updateMarkers(locations)
        }
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

    private fun updateMarkers(locations: List<CourierLocation>) {
        googleMap.clear()

        locations.forEach { location ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude!!, location.longitude!!))
                    .title(location.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(colorForUid(location.uid)))
            )
        }
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
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
