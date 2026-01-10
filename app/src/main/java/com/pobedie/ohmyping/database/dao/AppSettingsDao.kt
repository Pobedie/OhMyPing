package com.pobedie.ohmyping.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    @Query("SELECT is_listener_active FROM app_settings")
    fun getAppSettings(): Flow<Boolean>

    @Query("UPDATE app_settings SET is_listener_active = :isActive WHERE `key` = 1")
    suspend fun updateListenerActive(isActive: Boolean)

    // Initialize if not exists
    @Query("INSERT OR IGNORE INTO app_settings (`key`, is_listener_active) VALUES (1, :isActive)")
    suspend fun initializeIfNeeded(isActive: Boolean)
}