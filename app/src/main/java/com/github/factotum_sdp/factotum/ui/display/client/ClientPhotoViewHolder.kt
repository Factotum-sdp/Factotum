package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.databinding.DisplayItemPictureBinding

class ClientPhotoViewHolder(
    private val binding: DisplayItemPictureBinding,
    private val onShareClick: (Uri) -> Unit,
    private val onCardClick: (Uri) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var url: String? = null

    init {
        binding.shareButton.setOnClickListener {
            url?.let { urlString ->
                onShareClick(Uri.parse(urlString))
            }
        }

        binding.cardView.setOnClickListener {
            url?.let { urlString ->
                onCardClick(Uri.parse(urlString))
            }
        }
    }

    fun bind(photoName : String, url: String?) {
        this.url = url
        binding.displayItemView.text = extractNewName(photoName)
    }

    fun hideShareButton() {
        binding.shareButton.visibility = View.GONE
    }

    private fun extractNewName(name: String): String {
        val regex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""")
        val match = regex.find(name)
        return match?.value?.replace("_", " | ")?.replace('-', ':') ?: "Unknown date and time"
    }
}
