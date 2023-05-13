package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import com.github.factotum_sdp.factotum.databinding.DisplayItemPictureBinding
import com.github.factotum_sdp.factotum.ui.display.DisplayFragment
import com.github.factotum_sdp.factotum.ui.display.ReferenceDiffCallback
import com.google.firebase.storage.StorageReference

// Adapter for displaying photos in the recycler view
class ClientPhotoAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: ClientDisplayViewModel,
    private val onShareClick: (Uri) -> Unit = {},
    private val onCardClick: (Uri) -> Unit = {}
) : ListAdapter<StorageReference, ClientPhotoViewHolder>(ReferenceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientPhotoViewHolder {
        val binding = DisplayItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientPhotoViewHolder(binding, onShareClick, onCardClick)
    }


    override fun onBindViewHolder(holder: ClientPhotoViewHolder, position: Int) {
        val storageReference = getItem(position)

        viewModel.getUrlForPhoto(storageReference.path).observe(lifecycleOwner) { url ->
            holder.bind(storageReference, url)
        }
    }

}