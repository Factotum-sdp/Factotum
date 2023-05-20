package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.factotum_sdp.factotum.databinding.DisplayItemPictureBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(photoName : String, url: String?) {
        this.url = url
        val dateName = extractDateName(photoName)
        val timeName = extractTimeName(photoName)
        "Delivered the $dateName".also { binding.displayItemView.text = it }
        "at $timeName".also { binding.displayItemSecondaryView.text = it }

        Glide.with(binding.root)
            .load(url)
            .into(binding.displayItemPicture)
    }

    fun hideShareButton() {
        binding.shareButton.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extractDateName(name: String): String {
        val regex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""")
        val match = regex.find(name)
        if (match != null) {
            val dateTime = LocalDateTime.parse(match.value, DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"))
            val dayOfMonthSuffix = getDayOfMonthSuffix(dateTime.dayOfMonth)
            return dateTime.format(DateTimeFormatter.ofPattern("d'$dayOfMonthSuffix' 'of' MMMM yyyy"))
        }
        return "Unknown date"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extractTimeName(name: String): String {
        val regex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""")
        val match = regex.find(name)
        if (match != null) {
            val dateTime = LocalDateTime.parse(match.value, DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"))
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        return "Unknown time"
    }


    private fun getDayOfMonthSuffix(day: Int): String {
        if (day in 11..13) {
            return "th"
        }
        return when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}
