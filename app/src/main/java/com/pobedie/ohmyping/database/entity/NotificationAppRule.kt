package com.pobedie.ohmyping.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_app_rules")
data class NotificationAppRule(
    @PrimaryKey
    @ColumnInfo(name = "app_package")
    val appPackage: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "trigger_words")
    val triggerWords: String,

    @ColumnInfo(name = "vibration_pattern")
    val vibrationPattern: String,

    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
