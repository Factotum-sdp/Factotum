package com.github.factotum_sdp.factotum

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
class MapsMarkerActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val satellite = LatLng(46.520544, 6.567825)
        googleMap.addMarker(
            MarkerOptions()
                .position(satellite)
                .title("Satellite")
        )
        val epfl = LatLng(46.520536, 6.568318)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(epfl))

        // Add zoom controls to the map
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Add zoom gestures to the map
        googleMap.uiSettings.isZoomGesturesEnabled = true

        // Set a preference for minimum and maximum zoom.
        googleMap.setMinZoomPreference(6.0f)
        googleMap.setMaxZoomPreference(14.0f)

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val text = "Beer here at (46.520536, 6.568318)"

        // Check if a click count was set, then display the click count.
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_SHORT
        ).show()

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false
    }
}