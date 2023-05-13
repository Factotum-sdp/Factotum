package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemPictureBinding
import com.google.firebase.storage.StorageReference

class ClientPhotoViewHolder(
    private val binding: DisplayItemPictureBinding,
    private val onShareClick: (Uri) -> Unit,
    private val onCardClick: (Uri) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var storageReference: StorageReference? = null
    private var url: String? = null

    init {
        binding.shareButton.setOnClickListener {
            url?.let { urlString ->
                onShareClick(Uri.parse(urlString))
            }
        }

        binding.cardView.setOnClickListener {
            storageReference?.downloadUrl?.addOnSuccessListener { uri ->
                onCardClick(uri)
            }
        }
    }


    fun bind(storageReference: StorageReference, url: String?) {
        this.storageReference = storageReference
        this.url = url
        val dateTime = extractNewName(storageReference.name)
        binding.displayItemView.text = dateTime
    }


    private fun extractNewName(name: String): String {
        val regex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""")
        val match = regex.find(name)
        return match?.value?.replace("_", " | ")?.replace('-', ':') ?: "Unknown date and time"
    }
}
