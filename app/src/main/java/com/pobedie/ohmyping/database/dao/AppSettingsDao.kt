package com.pobedie.ohmyping.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.pobedie.ohmyping.database.entity.NotificationChannelRule
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    @Query("SELECT is_listener_active FROM app_settings")
    fun getAppSettings(): Flow<Boolean>

    @Query("UPDATE app_settings SET is_listener_active = :isActive WHERE `key` = 1")
    suspend fun updateListenerStatus(isActive: Boolean)
}