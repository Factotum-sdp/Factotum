package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.github.factotum_sdp.factotum.databinding.DisplayItemPictureBinding
import com.github.factotum_sdp.factotum.ui.display.ReferenceDiffCallback
import com.google.firebase.storage.StorageReference

// Adapter for displaying photos in the recycler view
class ClientPhotoAdapter(
    private val onShareClick: (StorageReference) -> Unit = {},
    private val onCardClick: (Uri) -> Unit = {}
) : ListAdapter<StorageReference, ClientPhotoViewHolder>(ReferenceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientPhotoViewHolder {
        val binding = DisplayItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientPhotoViewHolder(binding, onShareClick, onCardClick)
    }

    override fun onBindViewHolder(holder: ClientPhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}