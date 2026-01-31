package com.pobedie.ohmyping.screen

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.pobedie.ohmyping.R
import com.pobedie.ohmyping.database.AppRepository
import com.pobedie.ohmyping.entity.ApplicationChannel
import com.pobedie.ohmyping.entity.ApplicationItem
import com.pobedie.ohmyping.entity.UserApplication
import com.pobedie.ohmyping.entity.VibrationPattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.jvm.java

class MainViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {
    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.databaseInitialization()
            val userApps = getUserInstalledApps(application.applicationContext)
            val appItems = repository.applicationItems.first()
            val listenerIsEnabled = repository.isListenerActive.first()
            val loggingEnabled = repository.isLoggingActive.first()
            _viewState.update { state ->
                state.copy(
                    applicationItems = appItems,
                    notificationListenerEnabled = listenerIsEnabled,
                    loggingEnabled = loggingEnabled,
                    userApps = userApps,
                    filteredUserApps = userApps,
                )
            }
        }
    }

    fun switchListener() {
        if (_viewState.value.applicationItems.any{it.isEnabled}) {
            viewModelScope.launch { repository.switchNotificationListener(!_viewState.value.notificationListenerEnabled) }
            _viewState.update { state ->
                state.copy(notificationListenerEnabled = !state.notificationListenerEnabled )
            }
        } else {
            Toast.makeText(
                this@MainViewModel.application.applicationContext,
                application.applicationContext.getString(R.string.toast_must_have_app_enabled),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun switchLogger() {
        viewModelScope.launch { repository.switchLogging(!_viewState.value.loggingEnabled) }
        _viewState.update { state ->
            state.copy(loggingEnabled = !state.loggingEnabled)
        }
    }

    fun switchAppListener(app: ApplicationItem) {
        val newListenerState = !app.isEnabled
        viewModelScope.launch { repository.updateApplicationItem(app.copy(isEnabled = !app.isEnabled)) }
        _viewState.update { state ->
            val appList = state.applicationItems.toMutableList().map {
                if (it.packageName == app.packageName) {
                    it.copy(isEnabled = !it.isEnabled)
                } else it
            }
            state.copy(applicationItems = appList)
        }
        if (_viewState.value.applicationItems.none { it.isEnabled }) {
            _viewState.update { state ->
                viewModelScope.launch { repository.switchNotificationListener(false) }
                state.copy(notificationListenerEnabled = false)
            }
        } else if (_viewState.value.applicationItems.count { it.isEnabled } == 1 && newListenerState) {
            _viewState.update { state ->
                viewModelScope.launch { repository.switchNotificationListener(true) }
                state.copy(notificationListenerEnabled = true)
            }
        }
    }

    fun addChannel(app: ApplicationItem) {
        val channels = app.namedChannels.toMutableList()
        val newChannel = ApplicationChannel.emptyChannel()
        viewModelScope.launch { repository.insertChannelItem(app, newChannel)}
        channels.add( newChannel )
        val item = app.copy(
            namedChannels = channels
        )
        _viewState.update { state ->
            state.copy(
                applicationItems = state.applicationItems.map {
                    if (it.packageName == app.packageName) item else it
                },
                selectedAppChannelId = newChannel.id
            )
        }
    }

    fun switchChannelListener(app: ApplicationItem, channel: ApplicationChannel.NamedChannel) {
        viewModelScope.launch { repository.updateChannelItem(app, channel.copy(isEnabled = !channel.isEnabled)) }
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
                if (it.packageName == item.packageName) item else it
            })
        }
    }

    fun changeAppChannelName(app: ApplicationItem, channel: ApplicationChannel.NamedChannel, name: String) {
        viewModelScope.launch {
            repository.updateChannelItem(
                app,
                channel.copy(name = name)
            )
        }
        val newChannel = channel.copy(name = name)
        _viewState.update { state ->
            state.copy(applicationItems = state.applicationItems.map { _app ->
                if (_app.packageName == app.packageName) {
                    _app.copy(
                        namedChannels = _app.namedChannels.map { _channel ->
                            if (_channel.id == channel.id) {
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
                        if (_app.packageName == app.packageName) {
                            val newTriggerTexts = _app.allChannels.triggerText.toMutableList()
                            newTriggerTexts.add("")
                            val allChannels = _app.allChannels.copy(triggerText = newTriggerTexts)
                            viewModelScope.launch {
                                repository.updateApplicationItem(_app.copy(allChannels = allChannels))
                            }
                            _app.copy(allChannels = allChannels)
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.packageName == app.packageName) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        val newTriggerTexts = _channel.triggerText.toMutableList()
                                        newTriggerTexts.add("")
                                        viewModelScope.launch {
                                            repository.updateChannelItem(
                                                app,
                                                _channel.copy(triggerText = newTriggerTexts)
                                            )
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
                        if (_app.packageName == app.packageName) {
                            val newTriggerTexts = _app.allChannels.triggerText.mapIndexed { _index, _text ->
                                if (_index == index) triggerText else _text
                            }
                            val allChannels = _app.allChannels.copy(triggerText = newTriggerTexts)
                            viewModelScope.launch {
                                repository.updateApplicationItem(_app.copy(allChannels = allChannels))
                            }
                            _app.copy(allChannels = allChannels)
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.packageName == app.packageName) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        val newTriggerTexts = _channel.triggerText.mapIndexed { _index, _text ->
                                            if (_index == index) triggerText else _text
                                        }
                                        viewModelScope.launch {
                                            repository.updateChannelItem(
                                                app,
                                                _channel.copy(triggerText = newTriggerTexts)
                                            )
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
                        if (_app.packageName == app.packageName) {
                            val newTriggerTexts = _app.allChannels.triggerText.toMutableList()
                            newTriggerTexts.removeAt(index)
                            val allChannels = _app.allChannels.copy(triggerText = newTriggerTexts)
                            viewModelScope.launch {
                                repository.updateApplicationItem(_app.copy(allChannels = allChannels))
                            }
                            _app.copy(allChannels = allChannels)
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.packageName == app.packageName) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        val newTriggerTexts = _channel.triggerText.toMutableList()
                                        newTriggerTexts.removeAt(index)
                                        viewModelScope.launch {
                                            repository.updateChannelItem(
                                                app,
                                                _channel.copy(triggerText = newTriggerTexts)
                                            )
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

    fun changeAppChannelVibration(app: ApplicationItem, channel: ApplicationChannel, vibration: VibrationPattern) {
        _viewState.update { state ->
            if (channel is ApplicationChannel.AllChannels) {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.packageName == app.packageName) {
                            val allChannels = _app.allChannels.copy(vibrationPattern = vibration)
                            viewModelScope.launch {
                                repository.updateApplicationItem(_app.copy(allChannels = allChannels))
                            }
                            _app.copy(allChannels = allChannels)
                        } else _app
                    }
                )
            } else {
                state.copy(
                    applicationItems = state.applicationItems.map { _app ->
                        if (_app.packageName == app.packageName) {
                            _app.copy(
                                namedChannels = _app.namedChannels.map { _channel ->
                                    if (_channel.id == (channel as ApplicationChannel.NamedChannel).id) {
                                        viewModelScope.launch {
                                            repository.updateChannelItem(
                                                app,
                                                _channel.copy(vibrationPattern = vibration)
                                            )
                                        }
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

    fun changeAppChannelSelection(id: Long) {
        val newId = if (_viewState.value.selectedAppChannelId == id) 0L else id
        _viewState.update { state ->
            state.copy(selectedAppChannelId = newId)
        }
    }

    fun removeAppChannel(app: ApplicationItem, channelId: Long) {
        viewModelScope.launch { repository.deleteChannelItem(channelId) }
        _viewState.update { state ->
            state.copy(
                applicationItems = state.applicationItems.map { _app ->
                    if (_app.packageName == app.packageName) {
                        val newChannels = _app.namedChannels.filter { it.id != channelId }
                        _app.copy(namedChannels = newChannels)
                    } else _app
                },
                selectedAppChannelId = 0L
            )
        }
    }

    fun addApplication(app: UserApplication) {
        val packageManager = application.applicationContext.packageManager
        _viewState.update { state ->
            val appList = state.applicationItems.toMutableList()
            val iconOriginal = packageManager.getApplicationIcon(app.appInfo).toBitmap()
            val iconCompressed = compressBitmap(iconOriginal)
            val appItem = ApplicationItem(
                name = app.name,
                packageName = app.packageName,
                icon = iconCompressed,
                isEnabled = true,
                namedChannels = emptyList(),
                allChannels = ApplicationChannel.AllChannels(
                    triggerText = emptyList(),
                    vibrationPattern = VibrationPattern.BeeHive,
                ),
                creationTime = System.currentTimeMillis()
            )
            if (!appList.any { it.packageName == appItem.packageName }) {
                viewModelScope.launch { repository.insertApplicationItem(appItem) }
                appList.add(appItem)
            } else {
                Toast.makeText(
                    this@MainViewModel.application.applicationContext,
                    application.applicationContext.getString(R.string.toast_app_already_in_list),
                    Toast.LENGTH_SHORT
                ).show()
            }
            state.copy(
                applicationItems = appList,
                filteredUserApps = state.userApps
            )
        }

        if (!_viewState.value.notificationListenerEnabled) {
            viewModelScope.launch { repository.switchNotificationListener(true) }
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
                packageName = it.packageName,
                icon = null,
                appInfo = it.applicationInfo!!
            )
        }
    }
}

private fun compressBitmap(bitmap: Bitmap): ByteArray {
    val resizedBitmap = resizeBitmap(bitmap, 128, 128)
    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 70, outputStream)
    return outputStream.toByteArray()
}
private fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height

    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
}
