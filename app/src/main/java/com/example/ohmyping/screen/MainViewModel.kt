package com.example.ohmyping.screen

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.example.ohmyping.entity.ApplicationChannel
import com.example.ohmyping.entity.ApplicationItem
import com.example.ohmyping.entity.UserApplication
import com.example.ohmyping.entity.VibationPattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application ) {
    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    init {
        val userApps = getUserInstalledApps(application.applicationContext)
        _viewState.update { state ->
            state.copy(
                userApps = userApps,
                filteredUserApps = userApps,
                notificationListenerEnabled =
                    if (_viewState.value.applicationItems.isEmpty()) false else true
            )
        }
    }

    fun switchListener() {
        if (_viewState.value.applicationItems.any{it.isEnabled}) {
            _viewState.update { state ->
                state.copy(notificationListenerEnabled = !state.notificationListenerEnabled
                )
            }
        } else {
            Toast.makeText(
                this@MainViewModel.application.applicationContext,
                "You must have enabled app notification listener",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun switchAppListener(app: ApplicationItem) {
        // todo optimize
        val newIsEnabled = !app.isEnabled
        _viewState.update { state ->
            val appList = state.applicationItems.toMutableList().map {
                if (it == app) {
                    it.copy(isEnabled = !it.isEnabled)
                } else it
            }
            state.copy(applicationItems = appList)
        }
        if (_viewState.value.applicationItems.none { it.isEnabled }) {
            _viewState.update { state ->
                state.copy(notificationListenerEnabled = false)
            }
        } else if (_viewState.value.applicationItems.count { it.isEnabled } == 1 && newIsEnabled) {
            _viewState.update { state ->
                state.copy(notificationListenerEnabled = true)
            }
        }
    }

    fun addChannel(app: ApplicationItem) {
        val channels = app.namedChannels.toMutableList()
        channels.add(
            ApplicationChannel.Channel(
                id = UUID.randomUUID().toString(),
                name = "",
                isEnabled = true,
                triggerText = emptyList(),
                vibrationPattern = VibationPattern.BeeHive
            )
        )
        val item = app.copy(
            namedChannels = channels
        )
        _viewState.update { state ->
            state.copy(
                applicationItems = state.applicationItems.map {
                    if (it == app) item else it
                }
            )
        }
        println("DEBUG apps with new channel: ${_viewState.value.applicationItems}")
    }

    fun switchChannelListener(app: ApplicationItem, channel: ApplicationChannel.Channel) {
        val namedChannels = app.namedChannels.map {
            if (it == channel) {
                it.copy(isEnabled = !it.isEnabled)
            } else it
        }
        val item = app.copy(
            namedChannels = namedChannels
        )
        _viewState.update { state ->
            state.copy(applicationItems = state.applicationItems.map {
                if (it.name == item.name) item else it
            })
        }
    }

    fun changeAppChannelSelection(id: String) {
        val newId = if (_viewState.value.selectedAppChannelId == id) "" else id
        _viewState.update { state ->
            state.copy(selectedAppChannelId = newId)
        }
    }

    fun addApplication(app: UserApplication) {
        _viewState.update { state ->
            val appList = state.applicationItems.toMutableList()
            val appItem = ApplicationItem(
                id = UUID.randomUUID().toString(),
                name = app.name,
                icon = app.icon,
                isEnabled = true,
//                namedChannels = emptyList(),
                namedChannels = listOf(
                    ApplicationChannel.Channel(
                        id = UUID.randomUUID().toString(),
                        name = "test",
                        isEnabled = true,
                        triggerText = emptyList(),
                        vibrationPattern = VibationPattern.BeeHive
                    ),
                ),
                allChannels = ApplicationChannel.AllChannels(
                    isEnabled = true,
                    triggerText = emptyList(),
                    vibrationPattern = VibationPattern.BeeHive
                ),
            )
            if (!appList.contains(appItem)) {
                appList.add(appItem)
            } else {
                Toast.makeText(
                    this@MainViewModel.application.applicationContext,
                    "This app is already in the list",
                    Toast.LENGTH_SHORT
                ).show()
            }
            state.copy(
                applicationItems = appList
            )
        }

        if (!_viewState.value.notificationListenerEnabled) {
            _viewState.update { state -> state.copy(notificationListenerEnabled = true) }
        }

    }

    fun filterUserApplications(searchInput: String){
        _viewState.update { state ->
            state.copy(
                filteredUserApps = state.userApps.filter { it.name.lowercase().contains(searchInput.lowercase()) }
            )
        }
    }

    private fun getUserInstalledApps(context: Context): List<UserApplication> {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledPackages(0)
        // apps like Gmail are considered system apps therefore cant filter out sys apps, so just sort them to the bottom
        val sortedPackages = packages.sortedBy { packageInfo ->
            !( packageInfo.applicationInfo!!.flags and ApplicationInfo.FLAG_SYSTEM == 0 &&
               packageInfo.applicationInfo!!.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 0
             )
        }

        return sortedPackages.mapNotNull {
            UserApplication(
                name = packageManager.getApplicationLabel(it.applicationInfo!!).toString(),
                icon = packageManager.getApplicationIcon(it.applicationInfo!!).toBitmap(),
            )
        }
    }
}