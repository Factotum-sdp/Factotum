package com.github.factotum_sdp.factotum.ui.display.courier_boss

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.github.factotum_sdp.factotum.databinding.DisplayItemFolderBinding
import com.github.factotum_sdp.factotum.ui.display.ReferenceDiffCallback
import com.google.firebase.storage.StorageReference

class CourierBossFolderAdapter(
    private val onCardClick: (MutableLiveData<String>) -> Unit = {}
) : ListAdapter<StorageReference, CourierBossFolderViewHolder>(ReferenceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourierBossFolderViewHolder {
        val binding = DisplayItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourierBossFolderViewHolder(binding,  onCardClick)
    }

    override fun onBindViewHolder(holder: CourierBossFolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}