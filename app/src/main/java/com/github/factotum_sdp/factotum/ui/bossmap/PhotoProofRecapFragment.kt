package com.github.factotum_sdp.factotum.ui.bossmap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.display.DisplayFragment

/**
 * A simple DisplayFragment subclass. Used to show the photo proof recap of a delivery.
 */
class PhotoProofRecapFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val childFragment = DisplayFragment()
        childFragment.arguments = arguments
        childFragmentManager.beginTransaction()
            .add(R.id.photo_proof_recap_fragment, childFragment)
            .commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_proof_recap, container, false)
    }
}