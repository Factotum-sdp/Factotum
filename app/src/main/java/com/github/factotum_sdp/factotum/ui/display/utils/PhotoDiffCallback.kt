package com.github.factotum_sdp.factotum.ui.display.utils

import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.storage.StorageReference

// Callback to calculate the difference between two photo items
class PhotoDiffCallback : DiffUtil.ItemCallback<StorageReference>() {
    override fun areItemsTheSame(oldItem: StorageReference, newItem: StorageReference): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: StorageReference, newItem: StorageReference): Boolean {
        return oldItem.path == newItem.path
    }
}