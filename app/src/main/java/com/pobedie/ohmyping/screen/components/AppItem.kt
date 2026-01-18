package com.pobedie.ohmyping.screen.components

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pobedie.ohmyping.R
import com.pobedie.ohmyping.entity.ApplicationChannel
import com.pobedie.ohmyping.entity.ApplicationItem
import com.pobedie.ohmyping.entity.VibrationPattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppItem(
    applicationItem: ApplicationItem,
    selectedChannelId: Long,
    onSwitchListener: () -> Unit,
    onChannelSwitched: (ApplicationChannel.NamedChannel) -> Unit,
    onAddChannel: () -> Unit,
    onChangeChannelSelection: (id: Long) -> Unit,
    onChannelNameChanged: (ApplicationItem, ApplicationChannel.NamedChannel, name: String) -> Unit,
    onAddTriggerText: (ApplicationChannel) -> Unit,
    onTriggerTextChange: (ApplicationChannel, id: Int, text: String) -> Unit,
    onRemoveTriggerText: (ApplicationChannel, id: Int) -> Unit,
    onRemoveChannel: (id: Long) -> Unit,
    onVibrationPatternChanged: (ApplicationChannel, VibrationPattern) -> Unit
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
        if (applicationItem.icon != null) {
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                bitmap = applicationItem.icon.toBitmap(),
                contentDescription = "App icon"
            )
        } else {
            Box(Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceDim))
        }
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
        onCheckedChange = {onSwitchListener()},
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

        AllChannels(
          channel = applicationItem.allChannels,
          onTriggerTextChange = { index, value -> onTriggerTextChange(applicationItem.allChannels, index, value)},
          onAddTriggerText = {onAddTriggerText(applicationItem.allChannels)},
          onRemoveTriggerText = {onRemoveTriggerText(applicationItem.allChannels, it)},
          onVibrationPatternChange = {onVibrationPatternChanged(applicationItem.allChannels, it)}
        )
        HorizontalDivider()

          applicationItem.namedChannels.forEach { channel ->
              NamedChannels(
                  channel = channel,
                  selectedChannelId = selectedChannelId,
                  onChannelListenerSwitch = { onChannelSwitched(channel) },
                  onChannelNameChange = { onChannelNameChanged(applicationItem, channel, it) },
                  onTriggerTextChange = { index, value -> onTriggerTextChange(channel, index, value) },
                  onAddTriggerText = { onAddTriggerText(channel) },
                  onRemoveTriggerText = { onRemoveTriggerText(channel, it) },
                  onVibrationPatternChange = {onVibrationPatternChanged(channel, it)},
                  onChangeChannelSelection = { onChangeChannelSelection(it) },
                  onRemoveChannel = { onRemoveChannel(channel.id) }
              )
          }

          Row(
          modifier = Modifier
              .clip(RoundedCornerShape(22.dp))
              .fillMaxWidth()
              .clickable(onClick = { onAddChannel() }),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            modifier = Modifier.padding(14.dp),
            imageVector = Icons.Default.Add,
            contentDescription = "Add channel",
            tint = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = stringResource(R.string.add_channel),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }
  }
}

@Composable
private fun AllChannels(
    channel: ApplicationChannel.AllChannels,
    onTriggerTextChange: (Int, String) -> Unit,
    onAddTriggerText: () -> Unit,
    onRemoveTriggerText: (Int) -> Unit,
    onVibrationPatternChange: (VibrationPattern) -> Unit
) {
    var vibrationPopupOpened by remember { mutableStateOf(false) }
    Spacer(Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.all_notifications),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )
    Column(
        Modifier.padding(start = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.trigger_on_text),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalAlignment = Alignment.End
        ) {

        channel.triggerText.plus("").forEachIndexed { index, triggerText ->
            val isAdditionItem = index == channel.triggerText.lastIndex + 1
            InputField(
                inputValue = triggerText,
                placeholder = stringResource(R.string.trigger_text_placeholder),
                onInputChange = { onTriggerTextChange(index, it) },
                onAdd = { onAddTriggerText() },
                onTrailingIconClick = { if (isAdditionItem) onAddTriggerText() else onRemoveTriggerText(index) },
                isExpanded = !isAdditionItem,
            )
            Spacer(Modifier.height(4.dp))
        }
    }

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.vibration_pattern),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(8.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable(onClick = { vibrationPopupOpened = true })
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = channel.vibrationPattern.patternName,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                DropdownMenu(
                    expanded = vibrationPopupOpened,
                    onDismissRequest = { vibrationPopupOpened = false },
                    shape = RoundedCornerShape(8.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    VibrationPattern.entries.forEach { pattern ->
                        DropdownMenuItem(
                            text = { Text(pattern.patternName) },
                            onClick = { onVibrationPatternChange(pattern) },
                        )
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun NamedChannels(
    channel: ApplicationChannel.NamedChannel,
    selectedChannelId: Long,
    onChannelListenerSwitch: () -> Unit,
    onChannelNameChange: (String) -> Unit,
    onTriggerTextChange: (Int, String) -> Unit,
    onAddTriggerText: () -> Unit,
    onRemoveTriggerText: (Int) -> Unit,
    onVibrationPatternChange: (VibrationPattern) -> Unit,
    onChangeChannelSelection: (Long) -> Unit,
    onRemoveChannel: () -> Unit
) {
    val channelIsSelected = selectedChannelId == channel.id
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = {
                onChangeChannelSelection(channel.id)
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onChangeChannelSelection(channel.id)
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
            text = stringResource(R.string.channel),
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
            onCheckedChange = { onChannelListenerSwitch() }
        )
    }
    AnimatedVisibility(
        visible = channelIsSelected,
        enter = expandVertically() + fadeIn(initialAlpha = 0f),
        exit = shrinkVertically() + fadeOut(targetAlpha = 0f)
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            InputField(
                inputValue = channel.name,
                placeholder = stringResource(R.string.channel_placeholder),
                onInputChange = {onChannelNameChange(it)},
                onAdd = {},
                onTrailingIconClick = {onChannelNameChange("")},
                isExpanded = true,
            )
            Spacer(Modifier.height(12.dp))

            Column(
                Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.trigger_on_text),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    horizontalAlignment = Alignment.End
                ) {
                    channel.triggerText.plus("").forEachIndexed { index, triggerText ->
                        val isAdditionItem = index == channel.triggerText.lastIndex + 1
                        InputField(
                            inputValue = triggerText,
                            placeholder = stringResource(R.string.trigger_text_placeholder),
                            onInputChange = { onTriggerTextChange(index, it) },
                            onAdd = { onAddTriggerText() },
                            onTrailingIconClick = { if (isAdditionItem) onAddTriggerText() else onRemoveTriggerText(index) },
                            isExpanded = !isAdditionItem,
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = stringResource(R.string.vibration_pattern),
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
              text = channel.vibrationPattern.patternName,
              color = MaterialTheme.colorScheme.onSurface,
          )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                VibrationPattern.entries.forEach { pattern ->
                    DropdownMenuItem(
                        text = { Text(pattern.patternName) },
                        onClick = { onVibrationPatternChange(pattern) },
                    )
                }
            }
        }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onRemoveChannel() },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.error,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete_channel))
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
  HorizontalDivider(modifier = Modifier.padding(horizontal = 36.dp))
}

@Composable
fun ByteArray.toBitmap(): ImageBitmap {
    return this.let {
        BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
    }
}


@Preview
@Composable
private fun AppItemPreview() {
    val applicationItem1 = ApplicationItem(
//        icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
        icon = null,
        name = "Telegram",
        packageName = "Telegram",
        isEnabled = true,
        namedChannels = listOf(
            ApplicationChannel.NamedChannel(
                id = kotlin.random.Random.nextLong(),
                name = "Dog",
                isEnabled = false,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibrationPattern.BeeHive,
                creationTime = 1L
            ),
            ApplicationChannel.NamedChannel(
                id = kotlin.random.Random.nextLong(),
                name = "Dog",
                isEnabled = true,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibrationPattern.BeeHive,
                creationTime = 2L
            ),
        ),
        allChannels = ApplicationChannel.AllChannels(
            triggerText = listOf("Ice cream"),
            vibrationPattern = VibrationPattern.BeeHive
        ),
        creationTime = 3L
    )

    val applicationItem2 = ApplicationItem(
//        icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
        icon = null,
        name = "Telegram",
        packageName = "Telegram",
        isEnabled = false,
        namedChannels = listOf(
            ApplicationChannel.NamedChannel(
                id = 1L,
                name = "Dog",
                isEnabled = false,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibrationPattern.BeeHive,
                creationTime = 1L
            ),
            ApplicationChannel.NamedChannel(
                id = 0L,
                name = "Dog",
                isEnabled = true,
                triggerText = listOf("Cat", "Squirrel"),
                vibrationPattern = VibrationPattern.BeeHive,
                creationTime = 2L
            ),
        ),
        allChannels = ApplicationChannel.AllChannels(
            triggerText = listOf("Ice cream"),
            vibrationPattern = VibrationPattern.BeeHive
        ),
        creationTime = 4L
    )
    Column() {
        AppItem(
            applicationItem2,
            1,
            {},
            {},
            {},
            {},
            { _, _, _ -> },
            {},
            { _, _, _ -> },
            {_,_ ->},
            {},
            {_,_, ->}
        )
        Spacer(Modifier.height(11.dp))
        AppItem(
            applicationItem1,
            0,
            {},
            {},
            {},
            {},
            { _, _, _ -> },
            {},
            { _, _, _ -> },
            {_,_ ->},
            {},
            {_,_, ->}
        )
    }
}
