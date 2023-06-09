package com.apaluk.streamtheater.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mediaInfo")
data class MediaInfo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val mediaId: String,
    val title: String,
    val imageUrl: String?,
    val durationSeconds: Int,
)