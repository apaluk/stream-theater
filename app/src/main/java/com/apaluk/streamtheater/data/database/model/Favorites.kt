package com.apaluk.streamtheater.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorites(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val mediaId: String
)
