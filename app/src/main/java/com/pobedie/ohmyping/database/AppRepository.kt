package com.pobedie.ohmyping.database

import com.pobedie.ohmyping.database.dao.AppSettingsDao
import com.pobedie.ohmyping.database.dao.NotificationAppRuleDao
import com.pobedie.ohmyping.database.dao.NotificationChannelRuleDao
import com.pobedie.ohmyping.database.entity.NotificationAppRule
import com.pobedie.ohmyping.database.entity.NotificationChannelRule
import com.pobedie.ohmyping.entity.ApplicationChannel
import com.pobedie.ohmyping.entity.ApplicationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppRepository(
    private val notificationAppRuleDao: NotificationAppRuleDao,
    private val notificationChannelRuleDao: NotificationChannelRuleDao,
    private val appSettingsDao: AppSettingsDao,
) {

    suspend fun insertApplicationItem(appItem: ApplicationItem) =
        withContext(Dispatchers.IO) {
            val rule: NotificationAppRule = NotificationAppRule(
                appPackage = appItem.packageName,
                appName = appItem.name,
                appIcon = appItem.icon,
                triggerWords = appItem.allChannels.triggerText,
                vibrationPattern = appItem.allChannels.vibrationPattern,
                isActive = appItem.isEnabled,
                createdAt = System.currentTimeMillis()
            )
            if (notificationAppRuleDao.insertRule(rule) == -1L) updateApplicationItem(appItem)
        }

    suspend fun insertChannelItem(app: ApplicationItem, appChannel: ApplicationChannel.NamedChannel) =
        withContext(Dispatchers.IO) {
            val rule: NotificationChannelRule = NotificationChannelRule(
                id = appChannel.id.toLong(),
                appPackage = app.packageName,
                channelName = appChannel.name,
                triggerWords = appChannel.triggerText,
                vibrationPattern = appChannel.vibrationPattern,
                isActive = appChannel.isEnabled,
                createdAt = System.currentTimeMillis()
            )
            if (notificationChannelRuleDao.insertRule(rule) == -1L) updateChannelItem(app, appChannel)
        }

    suspend fun updateApplicationItem(appItem: ApplicationItem) =
        withContext(Dispatchers.IO){
            val rule: NotificationAppRule = NotificationAppRule(
                appPackage = appItem.packageName,
                appName = appItem.name,
                appIcon = appItem.icon,
                triggerWords = appItem.allChannels.triggerText,
                vibrationPattern = appItem.allChannels.vibrationPattern,
                isActive = appItem.isEnabled,
                createdAt = appItem.creationTime
            )
            notificationAppRuleDao.updateRule(rule)
    }

    suspend fun updateChannelItem(app: ApplicationItem, appChannel: ApplicationChannel.NamedChannel) =
        withContext(Dispatchers.IO) {
            val rule: NotificationChannelRule = NotificationChannelRule(
                id = appChannel.id.toLong(),
                appPackage = app.packageName,
                channelName = appChannel.name,
                triggerWords = appChannel.triggerText,
                vibrationPattern = appChannel.vibrationPattern,
                isActive = appChannel.isEnabled,
                createdAt = appChannel.creationTime
            )
            notificationChannelRuleDao.updateRule(rule)
        }

    suspend fun deleteChannelItem(channelId: Long) =
        withContext(Dispatchers.IO){
            notificationChannelRuleDao.deleteRuleById(channelId)
        }

    suspend fun switchNotificationListener(isActive: Boolean) =
        withContext(Dispatchers.IO) {
            // todo optimize settings db
            appSettingsDao.initializeIfNeeded(isActive)
            appSettingsDao.updateListenerActive(isActive)
        }

    val isListenerActive: Flow<Boolean> = appSettingsDao.getAppSettings()

    val applicationItems: Flow<List<ApplicationItem>> = combine(
        notificationAppRuleDao.getAllRules(),
        notificationChannelRuleDao.getAllRules(),
        ::Pair
    ).map { (apps, channels) ->
        val appItems = apps.map { app ->
            val filteredChannels = channels.filter { it.appPackage == app.appPackage }
            ApplicationItem(
                packageName = app.appPackage,
                name = app.appName,
                icon = app.appIcon,
                isEnabled = app.isActive,
                allChannels = ApplicationChannel.AllChannels(
                    triggerText = app.triggerWords,
                    vibrationPattern = app.vibrationPattern
                ),
                namedChannels = filteredChannels.map { _channel ->
                    ApplicationChannel.NamedChannel(
                        id = _channel.id,
                        name = _channel.channelName,
                        isEnabled = _channel.isActive,
                        triggerText = _channel.triggerWords,
                        vibrationPattern = _channel.vibrationPattern,
                        creationTime = _channel.createdAt
                    )
                },
                creationTime = app.createdAt
            )
        }
        return@map appItems
    }

}
