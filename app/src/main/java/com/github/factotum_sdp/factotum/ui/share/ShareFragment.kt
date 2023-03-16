package com.github.factotum_sdp.factotum.ui.share

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.R

class ShareFragment : Fragment() {

    private lateinit var viewModel: ShareViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share, container, false)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[ShareViewModel::class.java]

        // Find the button in the layout
        val shareButton: Button = view.findViewById(R.id.share_button)

        // Set the click listener for the button
        shareButton.setOnClickListener {
            viewModel.shareContent(requireContext())
        }

        return view
    }
}

