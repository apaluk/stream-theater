package com.apaluk.streamtheater.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apaluk.streamtheater.data.database.dao.MediaInfoDao
import com.apaluk.streamtheater.data.database.dao.SearchHistoryDao
import com.apaluk.streamtheater.data.database.dao.WatchHistoryDao
import com.apaluk.streamtheater.data.database.model.*

@Database(
    entities = [
        WatchHistory::class,
        SearchHistory::class,
        Stream::class,
        Favorites::class,
        MediaInfo::class
    ],
    version = 2,
    exportSchema = true
)
abstract class StDatabase: RoomDatabase() {
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE watchHistory ADD COLUMN isAutoSelected INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun mediaInfoDao(): MediaInfoDao
}