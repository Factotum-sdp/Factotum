package com.github.factotum_sdp.factotum.ui.display.utils

import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemBinding
import com.google.firebase.storage.StorageReference

class PhotoViewHolder(private val binding: DisplayItemBinding, private val onShareClick: (StorageReference) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    private val shareButton: ImageButton = binding.shareButton

    fun bind(storageReference: StorageReference) {
        // Extract the date, hour, and minute from the photo name
        val photoName = storageReference.name
        val dateTimeRegex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}:\d{2}""")
        val dateTimeMatch = dateTimeRegex.find(photoName)
        val dateTime = dateTimeMatch?.value?.replace("_", " | ")?.replace('-', ':') ?: "Unknown date and time"

        // Set the text of the TextView to the photo date, hour, and minute
        binding.displayItemView.text = dateTime

        shareButton.setOnClickListener {
            onShareClick(storageReference)
        }
    }
}
