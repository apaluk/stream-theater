package com.apaluk.streamtheater.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streams")
data class Stream(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val ident: String,
    val definition: Int,
    val speed: Float,
    val language: String?
)