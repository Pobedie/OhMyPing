package com.example.ohmyping.screen

import com.example.ohmyping.entity.ApplicationItem
import com.example.ohmyping.entity.UserApplication

data class ViewState(
    val applicationItems: List<ApplicationItem> = emptyList(),
    val notificationListenerEnabled: Boolean = true,
    val userApps: List<UserApplication> = emptyList(),
    val filteredUserApps: List<UserApplication> = emptyList(),
    val selectedAppChannelId: String = ""
) {
    // todo listener enabling logic:
//    no apps -> false
//    apps but user turned off -> false
//    else -> true
}