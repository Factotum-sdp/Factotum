package com.github.factotum_sdp.factotum.ui.display.data.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedPhotoDao {
    @Query("SELECT * FROM cached_photo WHERE folderName = :folderName")
    fun getAllByFolderName(folderName: String): List<CachedPhoto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg photos: CachedPhoto)
}
