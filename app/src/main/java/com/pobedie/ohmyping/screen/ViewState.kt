package com.pobedie.ohmyping.screen

import com.pobedie.ohmyping.entity.ApplicationItem
import com.pobedie.ohmyping.entity.UserApplication

data class ViewState(
    val applicationItems: List<ApplicationItem> = emptyList(),
    val notificationListenerEnabled: Boolean = true,
    val userApps: List<UserApplication> = emptyList(),
    val filteredUserApps: List<UserApplication> = emptyList(),
    val selectedAppChannelId: Long = 0
)