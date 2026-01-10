package com.pobedie.ohmyping

import android.app.Application
import android.content.Context
import com.pobedie.ohmyping.database.AppRepository
import com.pobedie.ohmyping.database.AppSettingsDatabase
import com.pobedie.ohmyping.database.NotificationAppRulesDatabase
import com.pobedie.ohmyping.database.NotificationChannelDatabase

class MainApp : Application() {

    val appContainer by lazy { AppContainer(this) }

    companion object {
        fun get(context: Context): MainApp = context.applicationContext as MainApp
    }
}

class AppContainer(private val application: Application) {

    private val notificationChannelDB: NotificationChannelDatabase by lazy {
        NotificationChannelDatabase.getDatabase(application)
    }

    private val notificationAppDB: NotificationAppRulesDatabase by lazy {
        NotificationAppRulesDatabase.getDatabase(application)
    }

    private val appSettingsDB: AppSettingsDatabase by lazy {
        AppSettingsDatabase.getDatabase(application)
    }

    val repository: AppRepository by lazy {
        AppRepository(
            notificationAppRuleDao = notificationAppDB.notificationAppRuleDao(),
            notificationChannelRuleDao = notificationChannelDB.notificationChannelRuleDao(),
            appSettingsDao = appSettingsDB.AppSettingsDao()
        )
    }

    fun provideMainViewModelFactory(): MainViewModelFactory {
        return MainViewModelFactory(application, repository)
    }
}