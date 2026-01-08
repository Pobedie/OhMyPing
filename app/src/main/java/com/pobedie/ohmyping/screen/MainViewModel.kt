package com.pobedie.ohmyping.screen

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.pobedie.ohmyping.entity.ApplicationChannel
import com.pobedie.ohmyping.entity.ApplicationItem
import com.pobedie.ohmyping.entity.UserApplication
import com.pobedie.ohmyping.entity.VibationPattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.jvm.java

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
        val newListenerState = !app.isEnabled
        _viewState.update { state ->
            val appList = state.applicationItems.toMutableList().map {
                if (it.id == app.id) {
                    it.copy(isEnabled = !it.isEnabled)
                } else it
            }
            state.copy(applicationItems = appList)
        }
        if (_viewState.value.applicationItems.none { it.isEnabled }) {
            _viewState.update { state ->
                state.copy(notificationListenerEnabled = false)
            }
        } else if (_viewState.value.applicationItems.count { it.isEnabled } == 1 && newListenerState) {
            _viewState.update { state ->
                state.copy(notificationListenerEnabled = true)
            }
        }
    }

    fun addChannel(app: ApplicationItem) {
        val channels = app.namedChannels.toMutableList()
        val channelId = UUID.randomUUID().toString()
        channels.add(
            ApplicationChannel.NamedChannel(
                id = channelId,
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
                    if (it.id == app.id) item else it
                },
                selectedAppChannelId = channelId
            )
        }
    }

    fun switchChannelListener(app: ApplicationItem, channel: ApplicationChannel.NamedChannel) {
        val namedChannels = app.namedChannels.map {
            if (it.id == channel.id) {
                it.copy(isEnabled = !it.isEnabled)
            } else it
        }
        val item = app.copy(
            namedChannels = namedChannels
        )
        _viewState.update { state ->
            state.copy(applicationItems = state.applicationItems.map {
                if (it.id == item.id) item else it
            })
        }
    }

    fun changeAppChannelName(app: ApplicationItem, channel: ApplicationChannel.NamedChannel, name: String) {
        val newChannel = channel.copy(name = name)
        println("DEBUG newChannel :  ${newChannel}")
        _viewState.update { state ->
            state.copy(applicationItems = state.applicationItems.map { _app ->
                if (_app.id == app.id){
                    _app.copy(
                        namedChannels = _app.namedChannels.map { _channel ->
                            if (_channel.id == channel.id) {
                                println("DEBUG newChannelTrigger")
                                newChannel
                            } else {
                                _channel
                            }
                        }
                    )
                } else {
                    _app
                }
            })
        }
    }

    fun addTriggerText(app: ApplicationItem, channel: ApplicationChannel) {
        _viewState.update { state ->
            if (channel is ApplicationChannel.AllChannels) {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            val newTriggerTexts = _app.allChannels.triggerText.toMutableList()
                            newTriggerTexts.add("")
                            _app.copy(allChannels = _app.allChannels.copy(triggerText = newTriggerTexts))
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        val newTriggerTexts = _channel.triggerText.toMutableList()
                                        newTriggerTexts.add("")
                                        _channel.copy(triggerText = newTriggerTexts)
                                    } else _channel
                                }
                            )
                        } else _app
                    }
                )
            }
        }
    }

    fun changeTriggerText(
        app: ApplicationItem,
        channel: ApplicationChannel,
        index: Int,
        triggerText: String
        ) {
        _viewState.update { state ->
            if (channel is ApplicationChannel.AllChannels) {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            val newTriggerTexts = _app.allChannels.triggerText.mapIndexed { _index, _text ->
                                if (_index == index) triggerText else _text
                            }
                            _app.copy(allChannels = _app.allChannels.copy(triggerText = newTriggerTexts))
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        val newTriggerTexts = _channel.triggerText.mapIndexed { _index, _text ->
                                            if (_index == index) triggerText else _text
                                        }
                                        _channel.copy(triggerText = newTriggerTexts)
                                    } else _channel
                                }
                            )
                        } else _app
                    }
                )
            }
        }
    }

    fun removeTriggerText(
        app: ApplicationItem,
        channel: ApplicationChannel,
        index: Int
    ) {
        _viewState.update { state ->
            if (channel is ApplicationChannel.AllChannels) {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            val newTriggerTexts = _app.allChannels.triggerText.toMutableList()
                            newTriggerTexts.removeAt(index)
                            _app.copy(allChannels = _app.allChannels.copy(triggerText = newTriggerTexts))
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        val newTriggerTexts = _channel.triggerText.toMutableList()
                                        newTriggerTexts.removeAt(index)
                                        _channel.copy(triggerText = newTriggerTexts)
                                    } else _channel
                                }
                            )
                        } else _app
                    }
                )
            }
        }
    }

    fun changeAppChannelVibration(app: ApplicationItem, channel: ApplicationChannel, vibration: VibationPattern) {
        _viewState.update { state ->
            if (channel is ApplicationChannel.AllChannels) {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            _app.copy(allChannels = _app.allChannels.copy(vibrationPattern = vibration))
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.id == app.id) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        _channel.copy(vibrationPattern = vibration)
                                    } else _channel
                                }
                            )
                        } else _app
                    }
                )
            }
        }

        val vibrator = application.applicationContext.getSystemService(Vibrator::class.java)

        viewModelScope.launch {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    vibration.timings, vibration.amplitudes, -1
                )
            )
        }
    }

    fun changeAppChannelSelection(id: String) {
        val newId = if (_viewState.value.selectedAppChannelId == id) "" else id
        _viewState.update { state ->
            state.copy(selectedAppChannelId = newId)
        }
    }

    fun removeAppChannel(app: ApplicationItem, id: String) {
        _viewState.update { state ->
            state.copy(
                applicationItems = state.applicationItems.map { _app ->
                    if (_app.id == app.id) {
                        val newChannels = _app.namedChannels.filter { it.id != id }
                        _app.copy(namedChannels = newChannels)
                    } else _app
                },
                selectedAppChannelId = ""
            )
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
                // todo remove
                namedChannels = listOf(
                    ApplicationChannel.NamedChannel(
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