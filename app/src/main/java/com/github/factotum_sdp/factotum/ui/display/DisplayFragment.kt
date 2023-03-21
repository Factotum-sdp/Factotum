package com.github.factotum_sdp.factotum.ui.display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemBinding
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding
import com.google.firebase.storage.StorageReference

// Fragment responsible for displaying a list of images from Firebase Storage
class DisplayFragment : Fragment() {

    // View model for this fragment
    private lateinit var viewModel: DisplayViewModel
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)

        // Initialize the view model
        viewModel = ViewModelProvider(this)[DisplayViewModel::class.java]

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

    // Adapter for displaying photos in the recycler view
    inner class PhotoAdapter : ListAdapter<StorageReference, PhotoViewHolder>(PhotoDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val binding = DisplayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PhotoViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    // Callback to calculate the difference between two photo items
    inner class PhotoDiffCallback : DiffUtil.ItemCallback<StorageReference>() {
        override fun areItemsTheSame(oldItem: StorageReference, newItem: StorageReference): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: StorageReference, newItem: StorageReference): Boolean {
            return oldItem.path == newItem.path
        }
    }

    // ViewHolder for displaying an individual photo item
    inner class PhotoViewHolder(private val binding: DisplayItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storageReference: StorageReference) {
            // Load the image using Glide and display it in the ImageView
            GlideApp.with(binding.displayItemView.context)
                .load(storageReference)
                .into(binding.displayItemView)
        }
    }
}
