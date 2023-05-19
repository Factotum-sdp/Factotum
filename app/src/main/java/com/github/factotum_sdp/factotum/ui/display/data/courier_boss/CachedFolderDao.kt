package com.github.factotum_sdp.factotum.ui.display.data.courier_boss

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedFolderDao {
    @Query("SELECT * FROM cached_folders")
    fun getAll(): List<CachedFolder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg folders: CachedFolder)

    @Delete
    fun deleteAll(folders: List<CachedFolder>)
}
