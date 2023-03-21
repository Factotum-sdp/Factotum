package com.github.factotum_sdp.factotum.ui.display.utils

import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemBinding
import com.github.factotum_sdp.factotum.ui.display.GlideApp
import com.google.firebase.storage.StorageReference


// ViewHolder for displaying an individual photo item
class PhotoViewHolder(private val binding: DisplayItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(storageReference: StorageReference) {
        // Load the image using Glide and display it in the ImageView
        GlideApp.with(binding.displayItemView.context)
            .load(storageReference)
            .into(binding.displayItemView)
    }
}