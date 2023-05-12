package com.github.factotum_sdp.factotum.ui.bossmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.CourierLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

private const val ZOOM_LEVEL_CITY = 14f
private const val SCALE_FACTOR_ICON = 0.7f

class BossMapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: BossMapViewModel by viewModels()
    private var cameraPositionInitialized = false
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_boss_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
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

        viewModel.courierLocations.observe(viewLifecycleOwner) { locations ->
            updateMarkers(locations)
            if (!cameraPositionInitialized) {
                val geometricMedian = calculateGeometricMedian(locations)
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        geometricMedian,
                        ZOOM_LEVEL_CITY
                    )
                )
                cameraPositionInitialized = true
            }
        }
    }


    private fun updateMarkers(locations: List<CourierLocation>) {
        googleMap.clear()

        locations.forEach { location ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude!!, location.longitude!!))
                    .title(location.name)
                    .icon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.directions_bike,
                            colorForUid(location.uid),
                            SCALE_FACTOR_ICON
                        )
                    )
            )
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int, color: Int,
        scaleFactor: Float): BitmapDescriptor {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setTint(color)

        val bitmap = Bitmap.createBitmap(
            (vectorDrawable!!.intrinsicWidth * scaleFactor).toInt(),
            (vectorDrawable.intrinsicHeight * scaleFactor).toInt(),
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun colorForUid(uid: String): Int {
        val uidHashed = uid.hashCode()

        val red = uidHashed and 0x00FF0000 shr 16
        val green = uidHashed and 0x0000FF00 shr 8
        val blue = uidHashed and 0x000000FF

        return Color.rgb(red, green, blue)
    }

    // Geometric Median approximation by taking the point with the smallest sum of distances to all
    // locations. Why this choice ? Because it's really less biases than the arithmetic mean or
    // the general median to outliers. See : https://www.youtube.com/watch?v=iy2RZbq7Kn4
    // even if still biases to malicious actors but doesn't matter here.
    private fun calculateGeometricMedian(locations: List<CourierLocation>): LatLng {
        var minSumDistance = Double.MAX_VALUE
        var geometricMedian = LatLng(0.0, 0.0)

        for (location1 in locations) {
            var sumDistance = 0.0
            for (location2 in locations) {
                val distance = Math.sqrt(
                    Math.pow(location1.latitude!! - location2.latitude!!, 2.0) +
                            Math.pow(location1.longitude!! - location2.longitude!!, 2.0)
                )
                sumDistance += distance
            }

            if (sumDistance < minSumDistance) {
                minSumDistance = sumDistance
                geometricMedian = LatLng(location1.latitude!!, location1.longitude!!)
            }
        }

        return geometricMedian
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

