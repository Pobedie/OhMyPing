package com.pobedie.ohmyping.database.dao

import com.pobedie.ohmyping.database.entity.NotificationAppRule
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationAppRuleDao {

    @Query("SELECT * FROM notification_app_rules ORDER BY created_at ASC")
    fun getAllRules(): Flow<List<NotificationAppRule>>

    @Query("SELECT * FROM notification_app_rules WHERE is_active = 1")
    fun getActiveRules(): Flow<List<NotificationAppRule>>

    @Query("SELECT * FROM notification_app_rules WHERE app_package = :packageName")
    fun getRulesByPackage(packageName: String): Flow<List<NotificationAppRule>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRule(rule: NotificationAppRule): Long

    @Update
    suspend fun updateRule(rule: NotificationAppRule)

    @Delete
    suspend fun deleteRule(rule: NotificationAppRule)

    @Query("DELETE FROM notification_app_rules WHERE app_package = :packageName")
    suspend fun deleteRuleByPackage(packageName: String)

    @Query("UPDATE notification_app_rules SET is_active = :isActive WHERE app_package = :packageName")
    suspend fun updateRuleStatus(packageName: String, isActive: Boolean)
}