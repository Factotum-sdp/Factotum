package com.github.factotum_sdp.factotum.ui.display.courier_boss

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemFolderBinding
import com.google.firebase.storage.StorageReference

class CourierBossFolderViewHolder(
    private val binding: DisplayItemFolderBinding,
    private val onCardClick: (MutableLiveData<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(storageReference: StorageReference) {
        // Set the text of the TextView to the photo date, hour, and minute
        binding.displayItemView.text = storageReference.name.replaceFirstChar { it.uppercase() }

        binding.cardView.setOnClickListener {
           //Do Nothing for now
            onCardClick(MutableLiveData(storageReference.name))
        }
    }
}