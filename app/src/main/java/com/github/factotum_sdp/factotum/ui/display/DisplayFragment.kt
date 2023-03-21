package com.github.factotum_sdp.factotum.ui.display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemBinding
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding
import com.github.factotum_sdp.factotum.ui.display.utils.PhotoAdapter
import com.google.firebase.storage.StorageReference

// Fragment responsible for displaying a list of images from Firebase Storage
class DisplayFragment : Fragment() {

    // View model for this fragment
    private val viewModel: DisplayViewModel by viewModels()
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)

        // Set up the recycler view with a photo adapter
        val photoAdapter = PhotoAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = photoAdapter
        }

        // Observe changes in the list of photo references and update the adapter
        viewModel.photoReferences.observe(viewLifecycleOwner) { photoReferences ->
            photoAdapter.submitList(photoReferences)
        }

        // Set up the refresh button click listener
        binding.refreshButton.setOnClickListener {
            viewModel.refreshImages()
        }

        return binding.root
    }

    // Clean up binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
