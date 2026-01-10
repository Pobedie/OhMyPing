@file:Suppress("DEPRECATION")

package com.pobedie.ohmyping.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
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
import android.provider.Settings
import androidx.compose.material3.Button
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pobedie.ohmyping.service.NotificationCaptureService

@Composable
fun MainApp() {
    val appContext = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val app = MainApp.get(appContext)

    val viewModel: MainViewModel = viewModel(
        factory = app.appContainer.provideMainViewModelFactory()
    )

    var showPermissionPopup by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (NotificationCaptureService.isNotificationAccessGranted(appContext, appContext.packageName)) {
                    NotificationCaptureService.startService(appContext)
                    showPermissionPopup = false
                } else {
                    showPermissionPopup = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showPermissionPopup) {
        ListenerPermissionPopup(
            onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(intent)
            }
        )
    }

    MainScreen(viewModel)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
        TopBar(
            listenerEnabled = state.notificationListenerEnabled,
            onClick = { viewModel.switchListener() }
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
                            text = "1. Select an app\n" +
                                    "2. Optionally enter chat or notification channel\n" +
                                    "3. Enter trigger words (like your name, nickname or any other word)\n" +
                                    "4. Select vibration pattern\n\n" +
                                    "Your phone will vibrate with custom vibration when app's notification " +
                                    "contains the trigger word",
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
                    Spacer(Modifier.height(16.dp))
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .innerShadow(
                        RoundedCornerShape(22.dp),
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
}

@Composable
private fun ListenerPermissionPopup(onClick:() -> Unit){
    Popup(popupPositionProvider = object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowSize: IntSize,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize
        ): IntOffset {
            return IntOffset.Zero
        }

    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
            ,
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
                        text = "For this app to work you must give it a permission to read notification content.",
                        textAlign = TextAlign.Justify
                    )
                    Button(
                        modifier = Modifier.padding(bottom = 16.dp),
                        onClick = onClick
                    ) {
                        Text("Open permission settings")
                    }
                }
            }
        )
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
            placeholder = "Search app",
            onInputChange = {
                searchInput = it
                onSearch(searchInput)
            },
            onAdd = {},
            onTrailingIconClick = {},
            isExpanded = true
        )
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
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .size(48.dp)
                            .clip(CircleShape),
                        bitmap = app.icon.asImageBitmap(),
                        contentDescription = null
                    )
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




@Preview
@Composable
private fun MainScreenPreview(){
    MainScreen()
}
