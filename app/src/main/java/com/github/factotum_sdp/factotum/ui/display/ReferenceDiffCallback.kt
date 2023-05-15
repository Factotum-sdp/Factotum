package com.github.factotum_sdp.factotum.ui.display

import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.storage.StorageReference

class ReferenceDiffCallback : DiffUtil.ItemCallback<StorageReference>() {

    override fun areItemsTheSame(oldItem: StorageReference, newItem: StorageReference): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: StorageReference, newItem: StorageReference): Boolean {
        return true
    }
}
