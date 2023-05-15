package com.github.factotum_sdp.factotum.ui.display.boss

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BossDisplayViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BossDisplayViewModel::class.java)) {
            return BossDisplayViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}