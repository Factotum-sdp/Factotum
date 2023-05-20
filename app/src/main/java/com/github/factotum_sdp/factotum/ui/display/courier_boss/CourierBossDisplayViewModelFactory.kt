package com.github.factotum_sdp.factotum.ui.display.courier_boss

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CourierBossDisplayViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourierBossDisplayViewModel::class.java)) {
            return CourierBossDisplayViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}