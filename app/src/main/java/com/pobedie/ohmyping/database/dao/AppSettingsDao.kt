package com.pobedie.ohmyping.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    @Query("SELECT is_listener_active FROM app_settings")
    fun listenerState(): Flow<Boolean>

    @Query("SELECT is_logging_active FROM app_settings")
    fun loggingState(): Flow<Boolean>

    @Query("UPDATE app_settings SET is_listener_active = :isActive WHERE `key` = 1")
    suspend fun updateListenerActive(isActive: Boolean)

    @Query("UPDATE app_settings SET is_logging_active = :isActive WHERE `key` = 1")
    suspend fun updateLoggingActive(isActive: Boolean)

    // Initialize if not exists
    @Query("INSERT OR IGNORE INTO app_settings (`key`, is_listener_active, is_logging_active) VALUES (1, :isListenerActive, :isLoggingActive)")
    suspend fun initializeIfNeeded(isListenerActive: Boolean, isLoggingActive: Boolean)
}