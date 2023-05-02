package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemBinding
import com.google.firebase.storage.StorageReference

class ClientPhotoViewHolder(
    private val binding: DisplayItemBinding,
    private val onShareClick: (StorageReference) -> Unit,
    private val onCardClick: (Uri) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(storageReference: StorageReference) {
        // Extract the date, hour, and minute from the photo name
        val dateTime = extractNewName(storageReference.name)
        // Set the text of the TextView to the photo date, hour, and minute
        binding.displayItemView.text = dateTime

        binding.shareButton.setOnClickListener {
            onShareClick(storageReference)
        }

        binding.cardView.setOnClickListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                onCardClick(uri)
            }
        }
    }

    // Extract the date, hour, and minute from the photo name
    private fun extractNewName(name: String): String {
        val regex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""")
        val match = regex.find(name)
        return match?.value?.replace("_", " | ")?.replace('-', ':') ?: "Unknown date and time"
    }
}
