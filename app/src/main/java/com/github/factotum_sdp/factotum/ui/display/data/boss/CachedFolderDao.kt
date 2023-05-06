package com.github.factotum_sdp.factotum.ui.display.data.boss

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedFolderDao {
    @Query("SELECT * FROM cached_folders")
    fun getAll(): List<CachedFolder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg folders: CachedFolder)

    @Query("DELETE FROM cached_folders")
    fun deleteAll()
}