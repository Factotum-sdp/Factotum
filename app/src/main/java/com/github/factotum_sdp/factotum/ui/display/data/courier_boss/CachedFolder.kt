package com.github.factotum_sdp.factotum.ui.display.data.courier_boss

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "folder", indices = [Index(value = ["name"])])
data class CachedFolder(
    @PrimaryKey val path: String,
    val name: String
)

