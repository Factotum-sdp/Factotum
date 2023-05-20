package com.github.factotum_sdp.factotum.ui.bossmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.CourierLocation
import com.github.factotum_sdp.factotum.models.DeliveryStatus
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

private const val ZOOM_LEVEL_CITY = 13f
private const val SCALE_FACTOR_ICON = 0.7f
private const val FACTOR_DARKER_COLOR = 0.7f
private const val MAIL_BOX_SIZE = 100

class BossMapFragment : Fragment(), OnMapReadyCallback {

    private var cameraPositionInitialized = false
    private val viewModel: BossMapViewModel by viewModels()
    private val contactsViewModel: ContactsViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap


    private lateinit var bitmapDeliveredScaled : Bitmap
    private lateinit var bitmapNotDeliveredScaled : Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_boss_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        bitmapDeliveredScaled = createScaledBitmap(
            BitmapFactory.decodeResource(requireContext().resources, R.drawable.mailbox_delivered),
            MAIL_BOX_SIZE, MAIL_BOX_SIZE, false)

        bitmapNotDeliveredScaled = createScaledBitmap(
            BitmapFactory.decodeResource(requireContext().resources, R.drawable.mailbox_lowered_flag),
            MAIL_BOX_SIZE, MAIL_BOX_SIZE, false)

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
            updateMap(locations, viewModel.deliveriesStatus.value ?: mapOf())
            if (!cameraPositionInitialized) {
                val geometricMedian = calculateMedianLocation()
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        geometricMedian,
                        ZOOM_LEVEL_CITY
                    )
                )
                cameraPositionInitialized = true

            }
        }

        viewModel.deliveriesStatus.observe(viewLifecycleOwner) { deliveryStatus ->
            updateMap(viewModel.courierLocations.value ?: emptyList(), deliveryStatus)
        }

        contactsViewModel.contacts.observe(viewLifecycleOwner) {
            viewModel.updateContacts(it)
        }

    }

    private fun updateMap(locations: List<CourierLocation>, deliveryStatus: Map<String, List<DeliveryStatus>>) {
        googleMap.clear()
        updateMarkers(locations)
        updateDestinationMarkers(deliveryStatus)
    }

    private fun updateDestinationMarkers(locations: Map<String, List<DeliveryStatus>>?) {
        locations?.forEach { entry ->
            val isFullyDelivered = entry.value.all { it.timeStamp != null }
            val listOfAllDeliveryStatus = StringBuilder()
            entry.value.forEach { status ->
                listOfAllDeliveryStatus.append("${status.courier} : ${status.destID}\n")
            }
            val coordinates = LatLng(entry.value[0].latitude!!, entry.value[0].longitude!!)
            googleMap.addMarker(
                MarkerOptions()
                    .position(coordinates)
                    .title(listOfAllDeliveryStatus.toString())
                    .icon(
                        if (isFullyDelivered)
                            BitmapDescriptorFactory.fromBitmap(bitmapDeliveredScaled)
                        else BitmapDescriptorFactory.fromBitmap(bitmapNotDeliveredScaled)
                    )
            )
        }
    }

    private fun updateMarkers(locations: List<CourierLocation>) {
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

        var red = uidHashed and 0x00FF0000 shr 16
        var green = uidHashed and 0x0000FF00 shr 8
        var blue = uidHashed and 0x000000FF

        red = (red * FACTOR_DARKER_COLOR).toInt()
        green = (green * FACTOR_DARKER_COLOR).toInt()
        blue = (blue * FACTOR_DARKER_COLOR).toInt()

        return Color.rgb(red, green, blue)
    }


    private fun calculateMedianLocation(): LatLng {
        val courierLocations: List<CourierLocation> = viewModel.courierLocations.value ?: emptyList()
        val deliveryStatus: Map<String, List<DeliveryStatus>> = viewModel.deliveriesStatus.value ?: emptyMap()

        val courierLatLngs = courierLocations.map { LatLng(it.latitude!!, it.longitude!!) }
        val deliveryLatLngs = deliveryStatus.flatMap {
                it.value
                    .filter { it.latitude != null && it.longitude != null }
                    .map { LatLng(it.latitude!!, it.longitude!!) }
        }


        val locations = if (courierLocations.isNotEmpty()) {
            courierLatLngs + deliveryLatLngs
        } else {
            deliveryLatLngs
        }
        return if (locations.isNotEmpty()) {
            val sortedLatitudes = locations.map { it.latitude }.sorted()
            val sortedLongitudes = locations.map { it.longitude }.sorted()

            val medianLatitude = sortedLatitudes[sortedLatitudes.size / 2]
            val medianLongitude = sortedLongitudes[sortedLongitudes.size / 2]

            LatLng(medianLatitude, medianLongitude)
        } else {
            LatLng(0.0, 0.0)
        }
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

