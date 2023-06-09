package com.apaluk.streamtheater.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistory")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val input: String,
    val date: Long,
    val count: Int = 0
)
