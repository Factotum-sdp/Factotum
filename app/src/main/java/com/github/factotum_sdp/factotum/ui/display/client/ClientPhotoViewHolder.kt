package com.github.factotum_sdp.factotum.ui.display.client

import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.factotum_sdp.factotum.databinding.DisplayItemPictureBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClientPhotoViewHolder(
    private val binding: DisplayItemPictureBinding,
    private val onShareClick: (Uri) -> Unit,
    private val onCardClick: (Uri) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private val dateTimeRegex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""").toPattern()
    }

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
        val dateTime = extractDateTime(photoName)
        val dateName = formatDateName(dateTime)
        val timeName = formatTimeName(dateTime)
        "Delivered the $dateName".also { binding.displayItemView.text = it }
        "at $timeName".also { binding.displayItemSecondaryView.text = it }

        Glide.with(binding.root)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(binding.displayItemPicture.measuredWidth, binding.displayItemPicture.measuredHeight)
            .fitCenter()
            .into(binding.displayItemPicture)
    }

    fun hideShareButton() {
        binding.shareButton.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extractDateTime(name: String): LocalDateTime? {
        val match = dateTimeRegex.matcher(name)
        if (match.find()) {
            return LocalDateTime.parse(match.group(), DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"))
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateName(dateTime: LocalDateTime?): String {
        dateTime?.let {
            val dayOfMonthSuffix = getDayOfMonthSuffix(it.dayOfMonth)
            return it.format(DateTimeFormatter.ofPattern("d'$dayOfMonthSuffix' 'of' MMMM yyyy"))
        }
        return "Unknown date"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTimeName(dateTime: LocalDateTime?): String {
        dateTime?.let {
            return it.format(DateTimeFormatter.ofPattern("HH:mm"))
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
