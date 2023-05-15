package com.github.factotum_sdp.factotum.ui.display.boss

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.github.factotum_sdp.factotum.databinding.DisplayItemFolderBinding
import com.github.factotum_sdp.factotum.ui.display.ReferenceDiffCallback
import com.google.firebase.storage.StorageReference

class BossFolderAdapter(
    private val onCardClick: (MutableLiveData<String>) -> Unit = {}
) : ListAdapter<StorageReference, BossFolderViewHolder>(ReferenceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BossFolderViewHolder {
        val binding = DisplayItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BossFolderViewHolder(binding,  onCardClick)
    }

    override fun onBindViewHolder(holder: BossFolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}