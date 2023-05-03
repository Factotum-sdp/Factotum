package com.github.factotum_sdp.factotum.ui.display.boss

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.github.factotum_sdp.factotum.databinding.DisplayItemFolderBinding
import com.google.firebase.storage.StorageReference

// Adapter for displaying photos in the recycler view
class BossFolderAdapter(
    private val onCardClick: (MutableLiveData<String>) -> Unit = {}
) : ListAdapter<StorageReference, BossFolderViewHolder>(BossFolderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BossFolderViewHolder {
        val binding = DisplayItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BossFolderViewHolder(binding,  onCardClick)
    }

    override fun onBindViewHolder(holder: BossFolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}