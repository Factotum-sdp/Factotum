package com.github.factotum_sdp.factotum.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


/**
 * Fragment class that represents the map
 */
class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val viewModel: MapsViewModel by activityViewModels()
    private val EPFL_LOC = LatLng(46.520536, 6.568318)


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

    private fun setMapProperties(){
        val mapFragment = binding.map.getFragment() as SupportMapFragment
        mapFragment.getMapAsync{ googleMap ->
            // clears map from previous markers
            googleMap.clear()
            //Shows destinations of selected roads
            for (route in viewModel.routes.value.orEmpty()){
                route.addDstToMap(googleMap)
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(EPFL_LOC))

            // Add zoom controls to the map
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Add zoom gestures to the map
            googleMap.uiSettings.isZoomGesturesEnabled = true

            // Sets zoom preferences
            googleMap.setMinZoomPreference(2.0f)
            googleMap.setMaxZoomPreference(14.0f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}