package com.example.ohmyping.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ohmyping.R
import com.example.ohmyping.entity.ApplicationChannel
import com.example.ohmyping.entity.ApplicationItem
import com.example.ohmyping.entity.VibationPattern
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ohmyping.entity.UserApplication
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
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

//        val applicationItems = listOf(
//            ApplicationItem(
//                icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
//                name = "Telegram",
//                isEnabled = true,
//                applicationChannels = listOf(
//                    ApplicationChannel(
//                        name = "Dog",
//                        isEnabled = true,
//                        triggerText = listOf("Cat", "Squirrel"),
//                        vibrationPattern = VibationPattern.BeeHive
//                    ),
//                    ApplicationChannel(
//                        name = "Cat",
//                        isEnabled = true,
//                        triggerText = listOf("Fish"),
//                        vibrationPattern = VibationPattern.BeeHive
//                    ),
//
//                    )
//            ),
//            ApplicationItem(
//                icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
//                name = "Gmail",
//                isEnabled = false,
//                applicationChannels = listOf(
//                    ApplicationChannel(
//                        name = "Dog",
//                        isEnabled = true,
//                        triggerText = listOf("Cat", "Squirrel"),
//                        vibrationPattern = VibationPattern.BeeHive
//                    ),
//                    ApplicationChannel(
//                        name = "Cat",
//                        isEnabled = true,
//                        triggerText = listOf("Fish"),
//                        vibrationPattern = VibationPattern.BeeHive
//                    ),
//
//                    )
//            )
//        )

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 10.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.16f)),
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
                        switchListener = { viewModel.switchAppListener(applicationItem) }
                    )
                    Spacer(Modifier.height(10.dp))
                }
                item {
                    if (state.applicationItems.isEmpty()) {
                        Text(
                            "1. Select an app\n" +
                                    "2. Optionally enter chat or notification channel\n" +
                                    "3. Enter trigger words (like your name, nickname or any other word)\n" +
                                    "4. Select vibration pattern\n\n" +
                                    "Your phone will vibrate with custom vibration when app's notification " +
                                    "contains the trigger word"
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

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    listenerEnabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp))
            .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.16f)),
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(Modifier.systemBarsPadding())
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                val buttonProgress = animateFloatAsState(
                    targetValue = if (listenerEnabled) 1f else 0f,
                    animationSpec = if (listenerEnabled) tween(500) else tween(500)
                )

                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .fillMaxWidth(0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(48.dp))
                            .background(MaterialTheme.colorScheme.inversePrimary)
                            .border(
                                4.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(48.dp)
                            )
                    )
                    Text(
                        modifier = Modifier
                            .padding(24.dp)
                            .matchParentSize(),
                        text = "OH\nMY\nPING",
                        minLines = 3,
                        maxLines = 3,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily(Font(R.font.roboto_condensed_bold_italic)),
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 40.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {

                        val infiniteTransition = rememberInfiniteTransition(label = "infiniteRotation")

                        val buttonRotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(8000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "buttonRotation"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryFixed)
                        )
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .combinedClickable(
                                    onClick = { onClick() },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .rotate(if (listenerEnabled) buttonRotation else 0f)
                            ,
                            progress = { buttonProgress.value },
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (listenerEnabled) "ON" else "OFF",
                            style = MaterialTheme.typography.headlineLargeEmphasized,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .innerShadow(
                    RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp),
                    shadow = Shadow(
                        radius = 36.dp,
                        offset = DpOffset(0.dp, 5.dp),
                        alpha = 0.2f
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppItem(
    applicationItem: ApplicationItem,
    switchListener: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                bitmap = applicationItem.icon.asImageBitmap(),
                contentDescription = "App icon"
            )
            Spacer(Modifier.width(16.dp))
            Text(
                modifier = Modifier.weight(1f, true),
                text = applicationItem.name,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Switch(
                applicationItem.isEnabled,
                onCheckedChange = {switchListener()},
                thumbContent = {
                    Icon(
                        modifier = Modifier.padding(3.dp),
                        imageVector = if (applicationItem.isEnabled) {
                            Icons.Default.Notifications
                        } else {
                            Icons.Default.Clear
                        },
                        contentDescription = null,
                        tint = if (applicationItem.isEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        }
                    )
                }
            )
        }

        AnimatedVisibility(applicationItem.isEnabled) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "All notifications:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Column(
                    Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Trigger on text",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        applicationItem.allChannels.triggerText.forEach { triggerText ->
                            InputField(
                                inputValue = triggerText,
                                placeholder = "Text to trigger ping",
                                onInputChange = {},
                                onAdd = {},
                                onTrailingIconClick = {},
                                isExpanded = true,
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                        InputField(
                            inputValue = "",
                            placeholder = "",
                            onInputChange = {},
                            onAdd = {},
                            onTrailingIconClick = {},
                            isExpanded = false,
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text ="Vibration pattern",
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            var expanded by remember { mutableStateOf(false) }
                            Spacer(Modifier.width(8.dp))

                            Text(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .weight(1f)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable(onClick = { expanded = true }),

                                text = "Bee Hive"
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                VibationPattern.entries.forEach { pattern ->
                                    DropdownMenuItem(
                                        text = { Text(pattern.patternName) },
                                        onClick = { /* Handle edit! */ },
                                    )
                                }
                            }
                        }
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()


                var selectedChannel by remember { mutableStateOf("") }
            applicationItem.namedChannels.forEach { channel ->
                val channelIsSelected = selectedChannel == channel.name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                selectedChannel = if (channelIsSelected) "" else channel.name
                            }
                        ) {
                            Icon(
                                modifier = Modifier.rotate(if (channelIsSelected) 180f else 0f),
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Expand button",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Text(
                            text = "Channel:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.weight(1f))
                        Checkbox(
                            checked = channel.isEnabled,
                            onCheckedChange = {}
                        )
                    }
                    AnimatedVisibility(channelIsSelected) {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            InputField(
                                inputValue = channel.name,
                                placeholder = "Chat or channel name",
                                onInputChange = {},
                                onAdd = {},
                                onTrailingIconClick = {},
                                isExpanded = true,
                            )

                            Spacer(Modifier.height(12.dp))

                            Column(
                                Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text ="Trigger on text",
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    channel.triggerText.forEach { triggerText ->
                                        InputField(
                                            inputValue = triggerText,
                                            placeholder = "Text to trigger ping",
                                            onInputChange = {},
                                            onAdd = {},
                                            onTrailingIconClick = {},
                                            isExpanded = true,
                                        )
                                        Spacer(Modifier.height(4.dp))
                                    }
                                    InputField(
                                        inputValue = "",
                                        placeholder = "",
                                        onInputChange = {},
                                        onAdd = {},
                                        onTrailingIconClick = {},
                                        isExpanded = false,
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text ="Vibration pattern",
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    var expanded by remember { mutableStateOf(false) }
                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .weight(1f)
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .clickable(onClick = { expanded = true }),

                                        text = channel.vibrationPattern.patternName
                                    )
                                    DropdownMenu (
                                        expanded = expanded,
                                        onDismissRequest = {expanded = false}
                                    ) {
                                        VibationPattern.entries.forEach { pattern ->
                                            DropdownMenuItem(
                                                text = { Text(pattern.patternName) },
                                                onClick = { /* Handle edit! */ },
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .fillMaxWidth()
                        .clickable(onClick = { /*add channel */ }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(14.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add channel",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Add channel",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                if (applicationItem.namedChannels.indexOf(channel) != applicationItem.namedChannels.lastIndex){
                    HorizontalDivider(modifier = Modifier.padding( horizontal = 36.dp))
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
            placeholder = "Search app",
            onInputChange = {
                searchInput = it
                onSearch(searchInput)
            },
            onAdd = {},
            onTrailingIconClick = {},
            isExpanded = true
        )
        LazyColumn(
//            modifier = Modifier.animateContentSize()
        ) {
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

@Composable
private fun InputField(
    inputValue: String,
    placeholder: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
    onTrailingIconClick: () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = if (isExpanded) {
            modifier
                .fillMaxWidth()
        } else {
            Modifier
                .widthIn(min = 100.dp)
                .clip(RoundedCornerShape(99.dp))
                .clickable(onClick = onAdd)
        },
        enabled = isExpanded,
        value = if (isExpanded) inputValue else "Add",
        textStyle = if (isExpanded) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyLarge,
        placeholder = { Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyLarge
        ) },
        onValueChange = { onInputChange(it) },
        shape = RoundedCornerShape(99.dp),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedIndicatorColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            disabledIndicatorColor = Color.Transparent,
        ),
        trailingIcon = {
            IconButton(
                onClick ={onTrailingIconClick()}
            ) {
                Icon(
                    modifier = Modifier.rotate(
                        if (isExpanded) 0f else 45f
                    ),
                    imageVector = Icons.Default.Clear,
                    contentDescription = if (isExpanded) "Delete" else "Add"
                )
            }
        }
    )
}


@Preview
@Composable
private fun InputFieldPreview(){
    Column() {
        InputField(
            "Test",
            "Test",
            {},
            {},
            onTrailingIconClick = {},
            false
        )
        InputField(
            "Test",
            "Test",
            {},
            {},
            onTrailingIconClick = {},
            true
        )
    }
}


@Preview
@Composable
private fun MainScreenPreview(){
    MainScreen()
}

@Preview
@Composable
private fun AppItemPreview(){
    val applicationItem1 = ApplicationItem(
        icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
        name = "Telegram",
        isEnabled = true,
        namedChannels = listOf(
            ApplicationChannel.Channel(
                name = "Dog",
                isEnabled = false,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibationPattern.BeeHive
            ),
            ApplicationChannel.Channel(
                name = "Dog",
                isEnabled = true,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibationPattern.BeeHive
            ),
        ),
        allChannels = ApplicationChannel.AllChannels(
            isEnabled = true,
            triggerText = listOf("Ice cream"),
            vibrationPattern = VibationPattern.BeeHive
        )
    )

    val applicationItem2 = ApplicationItem(
        icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
        name = "Telegram",
        isEnabled = false,
        namedChannels = listOf(
            ApplicationChannel.Channel(
                name = "Dog",
                isEnabled = false,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibationPattern.BeeHive
            ),
            ApplicationChannel.Channel(
                name = "Dog",
                isEnabled = true,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibationPattern.BeeHive
            ),
        ),
        allChannels = ApplicationChannel.AllChannels(
            isEnabled = true,
            triggerText = listOf("Ice cream"),
            vibrationPattern = VibationPattern.BeeHive
        )
    )
    Column() {
        AppItem(
            applicationItem2, {}
        )
        Spacer(Modifier.height(11.dp))
        AppItem(
            applicationItem1, {}
        )
    }
}