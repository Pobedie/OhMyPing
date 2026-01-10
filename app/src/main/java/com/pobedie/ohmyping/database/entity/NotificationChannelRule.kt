package com.pobedie.ohmyping.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.pobedie.ohmyping.entity.VibrationPattern

@Entity(tableName = "notification_channel_rules")
data class NotificationChannelRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "app_package")
    val appPackage: String,

    @ColumnInfo(name = "channel_name")
    val channelName: String,

    @ColumnInfo(name = "trigger_words")
    val triggerWords: List<String>,

    @ColumnInfo(name = "vibration_pattern")
    val vibrationPattern: VibrationPattern,

    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)