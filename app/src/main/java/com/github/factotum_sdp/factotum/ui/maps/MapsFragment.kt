package com.github.factotum_sdp.factotum.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentMapsBinding
import com.github.factotum_sdp.factotum.hasLocationPermission
import com.github.factotum_sdp.factotum.models.Route
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.Gson


/**
 * Fragment class that represents the map
 */
class MapsFragment : Fragment() {

    companion object {
        private val EPFL_LOC = LatLng(46.520536, 6.568318)
        private const val ZOOM_PADDING = 100
        private const val minZoom = 6.0f
        const val IN_NAV_PAGER = "nav_pager"
        const val ROUTE_NAV_KEY = "route"
        const val DRAW_ROUTE = "draw_route"
        const val MAPS_PKG = "com.google.android.apps.maps"
    }

    private var _binding: FragmentMapsBinding? = null
    private val viewModel: MapsViewModel by activityViewModels()
    private val rbViewModel: RoadBookViewModel by activityViewModels()
    private val contactsViewModel : ContactsViewModel by activityViewModels()
    private lateinit var mMap: GoogleMap
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requireContext().hasLocationPermission()
            activateLocation(mMap)
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
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.title_maps)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMapProperties()

    }

    private fun getDestinationsFromRoadbook() : List<Route> {
        val destinations = mutableListOf<Route>()
        rbViewModel.recordsListState.value?.let { records ->
            records.forEach { record ->
                val contact = contactsViewModel.contacts.value?.first{ it.username == record.clientID }
                if(contact?.latitude != null && contact.longitude != null){
                    val route = Route(0.0, 0.0, contact.latitude, contact.longitude, record.clientID)
                    destinations.add(route)
                }
            }

        }
        return destinations
    }

    private fun setMapProperties() {
        val mapFragment = binding.map.getFragment() as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap

            initMapLocation(googleMap)

            initMapUI(googleMap)
        }
    }

    private fun initMapLocation(googleMap: GoogleMap) {
        if (!requireContext().hasLocationPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        activateLocation(googleMap)
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
            mMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun initMapUI(googleMap: GoogleMap) {
        // clears map from previous markers
        googleMap.clear()

        if (arguments?.getBoolean(IN_NAV_PAGER) == true) {
            val route = arguments?.getString(ROUTE_NAV_KEY)?.let { Gson().fromJson(it, Route::class.java) }
            val drawRoute = arguments?.getBoolean(DRAW_ROUTE) ?: true
            route?.let {
                placeMarkers(listOf(it), googleMap, drawRoute)
            }
        }
        else {
            // places markers on the map and centers the camera
            val destinations = getDestinationsFromRoadbook()
            placeMarkers(destinations, googleMap)
        }
        // Add zoom controls to the map
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Add zoom gestures to the map
        googleMap.uiSettings.isZoomGesturesEnabled = true

        // Sets zoom preferences
        googleMap.setMinZoomPreference(minZoom)
    }

    private fun placeMarkers(routes: List<Route>?, googleMap: GoogleMap, drawRoutes : Boolean = true) {
        val bounds = LatLngBounds.Builder()


        for (route in routes.orEmpty()) {
            route.addSrcToMap(googleMap)
            route.addDstToMap(googleMap)
            if (drawRoutes) route.drawRoute(googleMap)
            bounds.include(route.dst)
        }

        googleMap.setOnPolylineClickListener { polyline ->
            val route = (polyline.tag as Route)
            googleMap.clear()
            route.addDstToMap(googleMap)
            route.addSrcToMap(googleMap)
            route.drawRoute(googleMap)
        }

        val padding = ZOOM_PADDING // offset from edges of the map in pixels

        val cuf = routes?.takeIf { it.isNotEmpty() }
            ?.run { CameraUpdateFactory.newLatLngBounds(bounds.build(), padding) }
            ?: CameraUpdateFactory.newLatLngZoom(EPFL_LOC, 8f)

        googleMap.moveCamera(cuf)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}