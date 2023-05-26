package com.github.factotum_sdp.factotum.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.databinding.FragmentMapsBinding
import com.github.factotum_sdp.factotum.hasLocationPermission
import com.github.factotum_sdp.factotum.models.Contact
import com.github.factotum_sdp.factotum.models.Route
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
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
    private val userViewModel : UserViewModel by activityViewModels()
    private lateinit var mMap: GoogleMap
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requireContext().hasLocationPermission()
            activateLocation(mMap)
        }
    }

    private var userLocationRoute: Polyline? = null
    private var currentUserLocation: LatLng? = null
    private var routeSelected: Boolean = false
    private var userRouteSelected: Boolean = false

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

    private fun getRoutesFromRoadbook() : List<Route> {
        val destinations = mutableListOf<Route>()
        rbViewModel.recordsListState.value?.let { records ->
            if(records.size == 1) {
                val firstRecord = records.first()
                val firstContact = contactsViewModel.contacts.value?.first{ it.username == firstRecord.clientID }
                if(firstContact?.hasCoordinates() == true){
                    val route = Route(firstContact.latitude ?: 0.0, firstContact.longitude ?: 0.0, firstContact.latitude ?: 0.0, firstContact.longitude ?: 0.0)
                    destinations.add(route)
                }
            }
            else {
                records.windowed(2, 1).forEach { recordList ->
                    val firstRecord = recordList.first()
                    val secondRecord = recordList.last()
                    val firstContact =
                        contactsViewModel.contacts.value?.first { it.username == firstRecord.clientID }
                    val secondContact =
                        contactsViewModel.contacts.value?.first { it.username == secondRecord.clientID }
                    if (firstContact?.hasCoordinates() == true && secondContact?.hasCoordinates() == true) {
                        val route = Route(
                            firstContact.latitude ?: 0.0,
                            firstContact.longitude ?: 0.0,
                            secondContact.latitude ?: 0.0,
                            secondContact.longitude ?: 0.0
                        )
                        destinations.add(route)
                    }
                }
            }

        }
        return destinations
    }

    private fun getContactsFromRoadbook() : List<Contact> {
        val contacts = mutableListOf<Contact>()
        rbViewModel.recordsListState.value?.let { records ->
            records.forEach { record ->
                val contact = contactsViewModel.contacts.value?.first{ it.username == record.clientID }
                if(contact != null) {
                    contacts.add(contact)
                }
            }

        }
        return contacts
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
        val contacts = getContactsFromRoadbook()
        placeContactsMarkers(googleMap, contacts)

        if (arguments?.getBoolean(IN_NAV_PAGER) == true) {
            val route = arguments?.getString(ROUTE_NAV_KEY)?.let { Gson().fromJson(it, Route::class.java) }
            val drawRoute = arguments?.getBoolean(DRAW_ROUTE) ?: false
            if(route != null && drawRoute) {
                drawRoutes(listOf(route), googleMap)
            }
        }
        else {
            // places markers on the map and centers the camera
            val routes = getRoutesFromRoadbook()
            drawRoutes(routes, googleMap)
        }
        // Add zoom controls to the map
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Add zoom gestures to the map
        googleMap.uiSettings.isZoomGesturesEnabled = true

        // Sets zoom preferences
        googleMap.setMinZoomPreference(minZoom)
    }

    private fun placeContactsMarkers(googleMap: GoogleMap, contacts: List<Contact>) {
        var noLocation = true
        val bounds = LatLngBounds.Builder()

        for (contact in contacts) {
            if (contact.hasCoordinates()) {
                noLocation = false
                bounds.include(LatLng(contact.latitude!!, contact.longitude!!))
                val marker = MarkerOptions()
                    .position(LatLng(contact.latitude, contact.longitude))
                    .title(contact.username)
                googleMap.addMarker(marker)
            }
        }

        val padding = ZOOM_PADDING // offset from edges of the map in pixels

        val cuf = if (!noLocation) {
                CameraUpdateFactory.newLatLngBounds(bounds.build(), padding) }
            else {
                CameraUpdateFactory.newLatLngZoom(EPFL_LOC, 8f)
            }

        googleMap.moveCamera(cuf)
    }

    private fun drawRoutes(routes: List<Route>, googleMap: GoogleMap) {
        userViewModel.userLocation.observe(viewLifecycleOwner) { userLocation ->
            userLocation?.let {
                currentUserLocation = LatLng(userLocation.latitude, userLocation.longitude) // Save the latest user location
                val currentRoute = Route(it.latitude, it.longitude, routes.first().src.latitude, routes.first().src.longitude)
                userLocationRoute?.remove() // Remove the old userLocation route
                userLocationRoute = currentRoute.drawRoute(googleMap, transparency = !userRouteSelected && routeSelected) // Draw and keep a reference to the new one
            }
        }

        googleMap.setOnPolylineClickListener { polyline ->
            val selRoute = (polyline.tag as Route)
            userRouteSelected = polyline == userLocationRoute
            updateRoutes(selRoute, routes, googleMap)
        }

        googleMap.setOnMapClickListener {
            routeSelected = false
            updateRoutes(null, routes, googleMap)
        }
    }

    private fun updateRoutes(selRoute : Route?, routes : List<Route>, googleMap: GoogleMap) {
        googleMap.clear()
        userLocationRoute = null // The userLocation route has been cleared from the map, so clear our reference too
        routeSelected = routeSelected || selRoute != null
        for (route in routes) {
            if (route == selRoute) route.drawRoute(googleMap)
            else route.drawRoute(googleMap, transparency = routeSelected)
        }

        // Draw the user route
        currentUserLocation?.let { userLocation ->
            if (routes.isNotEmpty()) {
                val userRoute = Route(userLocation.latitude, userLocation.longitude, routes.first().src.latitude, routes.first().src.longitude)
                userLocationRoute = userRoute.drawRoute(googleMap, transparency = !userRouteSelected && routeSelected)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}