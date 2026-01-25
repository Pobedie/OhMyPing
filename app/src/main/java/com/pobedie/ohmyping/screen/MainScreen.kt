package com.pobedie.ohmyping.screen

import android.Manifest
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pobedie.ohmyping.R
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pobedie.ohmyping.MainApp
import com.pobedie.ohmyping.entity.UserApplication
import com.pobedie.ohmyping.screen.components.AppItem
import com.pobedie.ohmyping.screen.components.InputField
import com.pobedie.ohmyping.screen.components.TopBar
import kotlinx.coroutines.launch
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.pobedie.ohmyping.service.NotificationCaptureService

@Composable
fun MainApp() {
    val appContext = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    val app = MainApp.get(appContext)

    val viewModel: MainViewModel = viewModel(
        factory = app.appContainer.provideMainViewModelFactory()
    )

    var showNotificationRequest by remember { mutableStateOf(false) }

    LaunchedEffect(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            showNotificationRequest = !hasNotificationPermission(context = appContext)
        }
    }

    if (showNotificationRequest && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) RequestNotificationPermission()

    var showPermissionPopup by remember { mutableStateOf(false) }
    var showBatteryOptimizationPopup by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (NotificationCaptureService.isNotificationAccessGranted(appContext, appContext.packageName)) {
                    NotificationCaptureService.startService(appContext)
                    showPermissionPopup = false
                } else {
                    showPermissionPopup = true
                }
                showBatteryOptimizationPopup = !powerManager.isIgnoringBatteryOptimizations(appContext.packageName)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    MainScreen(viewModel)

    if (showPermissionPopup) {
        ListenerPermissionPopup(
            onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(intent)
            }
        )
    }

    if (showBatteryOptimizationPopup && !showPermissionPopup) {
        BatteryOptimizationPopup(onClick = {openBatteryOptimizationSettings(appContext)})
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    var showLogs by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
        TopBar(
            listenerEnabled = state.notificationListenerEnabled,
            onClick = { viewModel.switchListener() },
            onShowLogs = {showLogs = true}
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 10.dp)
                .absoluteOffset(y = 1.dp) // hack to hide border at the bottom of the screen
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.16f))
                .border(
                    width = Dp.Hairline,
                    color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                ),
        ) {
            var showAppSelector by remember { mutableStateOf(false) }
            val bottomSheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            LazyColumn(
                contentPadding = PaddingValues(14.dp)
            ) {
                items(state.applicationItems) { applicationItem ->
                    AppItem(
                        applicationItem,
                        selectedChannelId = state.selectedAppChannelId,
                        onSwitchListener = { viewModel.switchAppListener(applicationItem) },
                        onChannelSwitched = { viewModel.switchChannelListener(applicationItem, it) },
                        onAddChannel = { viewModel.addChannel(applicationItem) },
                        onChangeChannelSelection = { viewModel.changeAppChannelSelection(it) },
                        onChannelNameChanged = { app, channel, name ->
                            viewModel.changeAppChannelName(
                                app,
                                channel,
                                name
                            )
                        },
                        onAddTriggerText = { viewModel.addTriggerText(applicationItem, it) },
                        onTriggerTextChange = { channel, index, text ->
                            viewModel.changeTriggerText(
                                app = applicationItem,
                                channel = channel,
                                index = index,
                                triggerText = text
                            )
                        },
                        onRemoveTriggerText = { channel, index ->
                            viewModel.removeTriggerText(applicationItem, channel, index)
                        },
                        onRemoveChannel = { viewModel.removeAppChannel(applicationItem, it) },
                        onVibrationPatternChanged = { channel, vibration ->
                            viewModel.changeAppChannelVibration(applicationItem, channel, vibration)
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                }
                item {
                    if (state.applicationItems.isEmpty()) {
                        Text(
                            text = stringResource(R.string.intro_to_app),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                if (state.applicationItems.isEmpty()) {
                                    RoundedCornerShape(26.dp)
                                } else {
                                    RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp,
                                        bottomStart = 26.dp,
                                        bottomEnd = 26.dp
                                    )
                                }
                            )
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(onClick = { showAppSelector = true }),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.padding(12.dp),
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Add application"
                        )
                    }
                    Spacer(Modifier
                        .height(16.dp)
                        .imePadding())
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .innerShadow(
                        RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                        shadow = Shadow(
                            radius = 36.dp,
                            offset = DpOffset(0.dp, 5.dp),
                            alpha = 0.2f
                        )
                    )
            )

            if (showAppSelector) {
                AppSelector(
                    bottomSheetState = bottomSheetState,
                    allUserApps = state.filteredUserApps,
                    onDismiss = {
                        showAppSelector = false
                        viewModel.filterUserApplications("")
                    },
                    onAddApplication = {
                        viewModel.addApplication(it)
                        scope.launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    showAppSelector = false
                                }
                            }
                    },
                    onSearch = { viewModel.filterUserApplications(it) }
                )
            }
        }
    }

    if (showLogs) {
        LogPopup(
            logContent =
                LocalContext.current.openFileInput("log.txt").bufferedReader().use { it.readText() } ,
            isLoggingEnabled = state.loggingEnabled,
            onEnable = {viewModel.switchLogger()},
            onDismiss = {showLogs = false}
        )
    }
}

@Composable
private fun ListenerPermissionPopup(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.intro_permission_request),
                    textAlign = TextAlign.Justify
                )
                Button(
                    modifier = Modifier.padding(bottom = 16.dp),
                    onClick = onClick
                ) {
                    Text(stringResource(R.string.open_permission_settings))
                }
            }
        }
    )
}

@Composable
private fun BatteryOptimizationPopup(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center,
        content = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.battery_optimization_request),
                    textAlign = TextAlign.Justify
                )
                Button(
                    modifier = Modifier.padding(bottom = 16.dp),
                    onClick = onClick
                ) {
                    Text(stringResource(R.string.open_battery_settings))
                }
            }
        }
    )
}

private fun openBatteryOptimizationSettings(context: Context) {
    val intent = Intent()

    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            // Standard Android approach
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", context.packageName, null)
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        }
        Build.MANUFACTURER.equals("xiaomi", ignoreCase = true) -> {
            // Xiaomi devices
            intent.setClassName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
            )
        }
        Build.MANUFACTURER.equals("samsung", ignoreCase = true) -> {
            // Samsung devices
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:${context.packageName}")
        }
        Build.MANUFACTURER.equals("huawei", ignoreCase = true) -> {
            // Huawei devices
            intent.setClassName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )
        }
        else -> {
            // Fallback for other devices
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:${context.packageName}")
        }
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to standard approach
        val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        fallbackIntent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(fallbackIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogPopup(
    logContent: String,
    isLoggingEnabled: Boolean,
    onEnable: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier .padding(vertical = 36.dp) ,
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier.then(
                    if (isLoggingEnabled) {
                        Modifier.fillMaxSize()
                    }   else {
                        Modifier.fillMaxWidth()
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notification logs",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "${logContent.count { it == '|' }} notifications",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Divider()

                if (isLoggingEnabled) {
                    Text(
                        text = logContent,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(scrollState),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Divider()

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        onClick = onEnable
                    ) {
                        Text(
                            text = if (isLoggingEnabled) "Disable" else "Enable logging"
                        )
                    }

                    if (logContent.isNotBlank() && isLoggingEnabled) {
                        FilledTonalButton(
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            onClick = {
                                scope.launch {
                                    scrollState.scrollTo(Int.MAX_VALUE)
                                }
                            }
                        ) {
                            Text(
                                text = "Scroll down"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppSelector(
    bottomSheetState: SheetState,
    allUserApps: List<UserApplication>,
    onDismiss: () -> Unit,
    onAddApplication: (UserApplication) -> Unit,
    onSearch: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = bottomSheetState
    ) {
        var searchInput by remember { mutableStateOf("") }
        InputField(
            modifier = Modifier.padding(horizontal = 8.dp),
            inputValue = searchInput,
            placeholder = stringResource(R.string.search_placeholder),
            onInputChange = {
                searchInput = it
                onSearch(searchInput)
            },
            onAdd = {},
            onTrailingIconClick = {},
            isExpanded = true
        )
        val packageManager = LocalContext.current.packageManager
        LazyColumn {
            item { Spacer(Modifier.height(16.dp)) }
            items(allUserApps) { app ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = { onAddApplication(app) }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = packageManager.getApplicationIcon(app.appInfo).toBitmap()
                    if (icon != null) {
                        Image(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .size(48.dp)
                                .clip(CircleShape),
                            bitmap = icon.asImageBitmap(),
                            contentDescription = null
                        )
                    } else {
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceDim)
                        )
                    }
                    Text(
                        text = app.name,
                        fontFamily = FontFamily(Font(R.font.roboto_bold)),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                    )
                }
            }
            item {
                Spacer(
                    Modifier
                        .imePadding()
                        .navigationBarsPadding()
                        .height(8.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun hasNotificationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun RequestNotificationPermission() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
}


@Preview
@Composable
private fun MainScreenPreview(){
    MainScreen()
}
