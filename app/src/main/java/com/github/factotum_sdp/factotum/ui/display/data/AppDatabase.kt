package com.github.factotum_sdp.factotum.ui.display.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.factotum_sdp.factotum.ui.display.data.courier_boss.CachedFolder
import com.github.factotum_sdp.factotum.ui.display.data.courier_boss.CachedFolderDao
import com.github.factotum_sdp.factotum.ui.display.data.client.CachedPhoto
import com.github.factotum_sdp.factotum.ui.display.data.client.CachedPhotoDao

@Database(entities = [CachedFolder::class, CachedPhoto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cachedFolderDao(): CachedFolderDao
    abstract fun cachedPhotoDao(): CachedPhotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "factotum_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}