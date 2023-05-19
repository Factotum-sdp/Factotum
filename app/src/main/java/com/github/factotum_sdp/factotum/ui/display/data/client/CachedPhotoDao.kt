package com.github.factotum_sdp.factotum.ui.display.data.client

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedPhotoDao {
    @Query("SELECT * FROM cached_photo WHERE folderName = :folderName")
    fun getAllByFolderName(folderName: String): List<CachedPhoto>


    @Query("SELECT * FROM cached_photo WHERE folderName = :folderName ORDER BY dateSortKey DESC")
    suspend fun getAllByFolderNameSortedByDate(folderName: String): List<CachedPhoto>

    @Query("SELECT * FROM cached_photo WHERE path = :photoPath LIMIT 1")
    suspend fun getPhotoByPath(photoPath: String): CachedPhoto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg photos: CachedPhoto)

    @Delete
    fun deleteAll(photos: List<CachedPhoto>)
}
