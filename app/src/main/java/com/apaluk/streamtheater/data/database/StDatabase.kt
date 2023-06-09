package com.apaluk.streamtheater.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
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
    version = 1,
    exportSchema = false
)
abstract class StDatabase: RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun mediaInfoDao(): MediaInfoDao
}