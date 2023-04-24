package com.github.factotum_sdp.factotum.ui.display.utils

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.github.factotum_sdp.factotum.databinding.DisplayItemBinding
import com.google.firebase.storage.StorageReference

// Adapter for displaying photos in the recycler view
class PhotoAdapter(
    private val onShareClick: (StorageReference) -> Unit = {},
    private val onCardClick: (Uri) -> Unit = {}
) : ListAdapter<StorageReference, PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = DisplayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding, onShareClick, onCardClick)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}