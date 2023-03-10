package com.github.factotum_sdp.factotum.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentSecondBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MapsFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val viewModel: MainView by activityViewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = binding.map.getFragment() as SupportMapFragment
        mapFragment.getMapAsync{ googleMap ->
            googleMap.clear()
            for (route in viewModel.routes.value.orEmpty()){
                route.addToMap(googleMap)
            }
            val epfl = LatLng(46.520536, 6.568318)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(epfl))

            // Add zoom controls to the map
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Add zoom gestures to the map
            googleMap.uiSettings.isZoomGesturesEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}