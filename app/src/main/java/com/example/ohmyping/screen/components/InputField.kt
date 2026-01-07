package com.example.ohmyping.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InputField(
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

