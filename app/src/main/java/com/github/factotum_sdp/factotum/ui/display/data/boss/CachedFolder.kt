package com.github.factotum_sdp.factotum.ui.display.data.boss

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_folders")
data class CachedFolder(
    @PrimaryKey
    val path : String,
)