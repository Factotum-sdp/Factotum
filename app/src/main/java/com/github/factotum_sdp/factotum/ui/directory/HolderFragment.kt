package com.github.factotum_sdp.factotum.ui.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.maps.MapsFragment

/**
 * A simple DisplayFragment subclass. Used to show the photo proof recap of a delivery.
 */
class HolderFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val childFragment = MapsFragment()
        childFragment.arguments = arguments
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_holder, childFragment)
            .commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_holder, container, false)
    }
}