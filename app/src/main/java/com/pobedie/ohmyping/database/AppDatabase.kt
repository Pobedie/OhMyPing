package com.pobedie.ohmyping.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pobedie.ohmyping.database.dao.AppSettingsDao
import com.pobedie.ohmyping.database.dao.NotificationAppRuleDao
import com.pobedie.ohmyping.database.dao.NotificationChannelRuleDao
import com.pobedie.ohmyping.database.entity.AppSettings
import com.pobedie.ohmyping.database.entity.NotificationAppRule
import com.pobedie.ohmyping.database.entity.NotificationChannelRule

@Database(
    entities = [NotificationAppRule::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotificationAppRulesDatabase : RoomDatabase() {

    abstract fun notificationAppRuleDao(): NotificationAppRuleDao

    companion object {
        @Volatile
        private var INSTANCE: NotificationAppRulesDatabase? = null

        fun getDatabase(context: Context): NotificationAppRulesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotificationAppRulesDatabase::class.java,
                    "notification_monitor_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Database(
    entities = [NotificationChannelRule::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotificationChannelDatabase : RoomDatabase() {

    abstract fun notificationChannelRuleDao(): NotificationChannelRuleDao

    companion object {
        @Volatile
        private var INSTANCE: NotificationChannelDatabase? = null

        fun getDatabase(context: Context): NotificationChannelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotificationChannelDatabase::class.java,
                    "notification_monitor_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Database(
    entities = [AppSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppSettingsDatabase : RoomDatabase() {

    abstract fun AppSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppSettingsDatabase? = null

        fun getDatabase(context: Context): AppSettingsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppSettingsDatabase::class.java,
                    "notification_monitor_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
