package com.pobedie.ohmyping.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pobedie.ohmyping.database.entity.NotificationChannelRule
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationChannelRuleDao {

    @Query("SELECT * FROM notification_channel_rules ORDER BY created_at ASC")
    fun getAllAppRules(): Flow<List<NotificationChannelRule>>

    @Query("SELECT * FROM notification_channel_rules WHERE is_active = 1")
    fun getActiveRules(): Flow<List<NotificationChannelRule>>

    @Query("SELECT * FROM notification_channel_rules WHERE app_package = :packageName")
    fun getRulesByPackage(packageName: String): Flow<List<NotificationChannelRule>>

    @Insert(onConflict = OnConflictStrategy.IGNORE) // todo catch -1 and perform Update instead
    suspend fun insertRule(rule: NotificationChannelRule): Long

    @Update
    suspend fun updateRule(rule: NotificationChannelRule)

    @Delete
    suspend fun deleteRule(rule: NotificationChannelRule)

    @Query("DELETE FROM notification_channel_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Long)

    @Query("UPDATE notification_channel_rules SET is_active = :isActive WHERE id = :id")
    suspend fun updateRuleStatus(id: Long, isActive: Boolean)
}