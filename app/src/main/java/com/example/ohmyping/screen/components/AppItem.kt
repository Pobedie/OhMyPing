package com.example.ohmyping.screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.example.ohmyping.R
import com.example.ohmyping.entity.ApplicationChannel
import com.example.ohmyping.entity.ApplicationItem
import com.example.ohmyping.entity.VibationPattern
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppItem(
  applicationItem: ApplicationItem,
  selectedChannelId: String,
  onSwitchListener: () -> Unit,
  onChannelSwitched: (ApplicationChannel.Channel) -> Unit,
  onAddChannel: () -> Unit,
  onChangeChannelSelection: (String) -> Unit
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

// ALL CHANNELS SETTINGS
        AllChannels(
          channel = applicationItem.allChannels,
          onTriggerTextChange = { index, value -> },
          onAddTriggerText = {},
          onVibrationPatternChange = {}
        )
        HorizontalDivider()

// NAMED CHANNELS SETTINGS
        applicationItem.namedChannels.forEach { channel ->
          NamedChannels(
            channel = channel,
            selectedChannelId = selectedChannelId,
            onChannelListenerSwitch = {onChannelSwitched(channel)},
            onChannelNameChange = {},
            onTriggerTextChange = { index, value -> },
            onAddTriggerText = {},
            onVibrationPatternChange = {},
            onChangeChannelSelection ={onChangeChannelSelection(it)}
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
            text = "Add channel",
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
  onVibrationPatternChange: (VibationPattern) -> Unit
) {
  Spacer(Modifier.height(16.dp))
  Text(
    text = "All notifications:",
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.onSurface,
    fontWeight = FontWeight.Bold
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
}

@Composable
private fun NamedChannels(
  channel: ApplicationChannel.Channel,
  selectedChannelId: String,
  onChannelListenerSwitch: () -> Unit,
  onChannelNameChange: (String) -> Unit,
  onTriggerTextChange: (Int, String) -> Unit,
  onAddTriggerText: () -> Unit,
  onVibrationPatternChange: (VibationPattern) -> Unit,
  onChangeChannelSelection: (String) -> Unit
) {
  val channelIsSelected = selectedChannelId == channel.id
  Row(verticalAlignment = Alignment.CenterVertically) {
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
      onCheckedChange = {onChannelListenerSwitch()}
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
      Spacer(Modifier.height(8.dp))
    }
  }
  HorizontalDivider(modifier = Modifier.padding(horizontal = 36.dp))
}


@Preview
@Composable
private fun AppItemPreview(){
  val applicationItem1 = ApplicationItem(
    id = UUID.randomUUID().toString(),
    icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
    name = "Telegram",
    isEnabled = true,
    namedChannels = listOf(
      ApplicationChannel.Channel(
        id = UUID.randomUUID().toString(),
        name = "Dog",
        isEnabled = false,
        triggerText = listOf("Cat", "Squirrel"),
        vibrationPattern = VibationPattern.BeeHive
      ),
      ApplicationChannel.Channel(
        id = UUID.randomUUID().toString(),
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
    id = UUID.randomUUID().toString(),
    icon = (R.drawable.telegram_logo).toDrawable().toBitmap(),
    name = "Telegram",
    isEnabled = false,
    namedChannels = listOf(
      ApplicationChannel.Channel(
        id = UUID.randomUUID().toString(),
        name = "Dog",
        isEnabled = false,
        triggerText = listOf("Cat", "Squirrel"),
        vibrationPattern = VibationPattern.BeeHive
      ),
      ApplicationChannel.Channel(
        id = UUID.randomUUID().toString(),
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
      applicationItem2, "", {}, {}, {}, {}
    )
    Spacer(Modifier.height(11.dp))
    AppItem(
      applicationItem1, "", {}, {}, {}, {}
    )
  }
}
