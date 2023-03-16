package com.github.factotum_sdp.factotum.ui.share

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel

class ShareViewModel : ViewModel() {

    fun shareContent(context: Context) {
        // Set the content you want to share
        val shareText = "Check out this amazing content!"

        // Create a share intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        // Start the share activity with the intent
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}
