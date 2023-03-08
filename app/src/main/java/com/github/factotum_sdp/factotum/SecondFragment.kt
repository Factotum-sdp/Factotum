package com.github.factotum_sdp.factotum

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.databinding.FragmentSecondBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

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
            val epfl = LatLng(46.520536, 6.568318)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(epfl))

            // Add zoom controls to the map
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Add zoom gestures to the map
            googleMap.uiSettings.isZoomGesturesEnabled = true

            // Set a preference for minimum and maximum zoom.
            googleMap.setMinZoomPreference(6.0f)
            googleMap.setMaxZoomPreference(14.0f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}