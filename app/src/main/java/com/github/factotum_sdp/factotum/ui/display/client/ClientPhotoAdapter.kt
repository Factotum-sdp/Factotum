package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import com.github.factotum_sdp.factotum.databinding.DisplayItemPictureBinding
import com.github.factotum_sdp.factotum.model.Role
import com.github.factotum_sdp.factotum.ui.display.ReferenceDiffCallback
import com.google.firebase.storage.StorageReference

class ClientPhotoAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: ClientDisplayViewModel,
    private val userRole: Role,
    private val onShareClick: (Uri) -> Unit = {},
    private val onCardClick: (Uri) -> Unit = {}
) : ListAdapter<StorageReference, ClientPhotoViewHolder>(ReferenceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientPhotoViewHolder {
        val binding = DisplayItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientPhotoViewHolder(binding, onShareClick, onCardClick)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ClientPhotoViewHolder, position: Int) {
        val pictureReference : StorageReference = getItem(position)

        viewModel.getUrlForPhoto(pictureReference.path).observe(lifecycleOwner) { url ->
            holder.bind(pictureReference.name, url)

            if(userRole == Role.CLIENT) {
                holder.hideShareButton()
            }
        }
    }
}