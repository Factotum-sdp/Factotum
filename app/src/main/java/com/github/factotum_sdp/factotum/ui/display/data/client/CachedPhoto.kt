package com.github.factotum_sdp.factotum.ui.display.data.client

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cached_photo", indices = [Index(value = ["dateSortKey"])])
data class CachedPhoto(
    @PrimaryKey val path: String,
    val folderName: String,
    val url: String,
    val dateSortKey : Long
)

