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
import com.bumptech.glide.Glide
import com.github.factotum_sdp.factotum.databinding.DisplayItemBinding
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding

class DisplayFragment : Fragment() {

    private lateinit var viewModel: DisplayViewModel
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[DisplayViewModel::class.java]

        val photoAdapter = PhotoAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = photoAdapter
        }

        viewModel.photoUrls.observe(viewLifecycleOwner) { photoUrls ->
            photoAdapter.submitList(photoUrls)
        }

        // Set up the refresh button click listener
        binding.refreshButton.setOnClickListener {
            viewModel.refreshImages()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class PhotoAdapter : ListAdapter<String, PhotoViewHolder>(PhotoDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val binding = DisplayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PhotoViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    inner class PhotoDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    inner class PhotoViewHolder(private val binding: DisplayItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            Glide.with(binding.displayItemView.context)
                .load(url)
                .into(binding.displayItemView)
        }
    }
}
