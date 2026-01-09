package com.pobedie.ohmyping.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val key: Int = 1,

    @ColumnInfo(name = "is_listener_active", defaultValue = "1")
    val isListenerActive: Boolean = true,
)
