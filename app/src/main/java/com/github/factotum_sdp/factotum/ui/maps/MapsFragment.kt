package com.github.factotum_sdp.factotum.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.github.factotum_sdp.factotum.data.localisation.Route
import com.github.factotum_sdp.factotum.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds


/**
 * Fragment class that represents the map
 */
class MapsFragment : Fragment() {

    companion object {
        private val EPFL_LOC = LatLng(46.520536, 6.568318)
        private const val ZOOM_PADDING = 100
        private const val minZoom = 6.0f
    }

    private var _binding: FragmentMapsBinding? = null
    private val viewModel: MapsViewModel by activityViewModels()
    private lateinit var mMap : GoogleMap
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted){
            checkAndActivateLocation()
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMapProperties()

    }

    private fun setMapProperties() {
        val mapFragment = binding.map.getFragment() as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap

            if (!checkAndActivateLocation()){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            // clears map from previous markers
            googleMap.clear()

            // places markers on the map and centers the camera
            placeMarkers(viewModel.routesState, googleMap)

            // Add zoom controls to the map
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Add zoom gestures to the map
            googleMap.uiSettings.isZoomGesturesEnabled = true

            // Sets zoom preferences
            googleMap.setMinZoomPreference(minZoom)
        }
    }

    private fun placeMarkers(routes: LiveData<List<Route>>, googleMap: GoogleMap) {
        val bounds = LatLngBounds.Builder()

        for (route in routes.value.orEmpty()) {
            route.addDstToMap(googleMap)
            bounds.include(route.dst)
        }

        val padding = ZOOM_PADDING // offset from edges of the map in pixels

        val cuf = routes.value?.takeIf { it.isNotEmpty() }
            ?.run { CameraUpdateFactory.newLatLngBounds(bounds.build(), padding) }
            ?: CameraUpdateFactory.newLatLngZoom(EPFL_LOC, 8f)

        googleMap.moveCamera(cuf)
    }

    private fun checkAndActivateLocation() :Boolean{
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return true
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}