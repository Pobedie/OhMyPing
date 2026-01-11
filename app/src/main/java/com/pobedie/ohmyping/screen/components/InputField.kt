package com.pobedie.ohmyping.screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pobedie.ohmyping.R

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
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            if (inputValue.isBlank()) focusRequester.requestFocus()
        }
    }
    val animatedWidth = animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.3f,
    )
    val animatedRotation = animateFloatAsState(
        targetValue = if (isExpanded) 0f else 45f
    )

    TextField(
        modifier = modifier
            .fillMaxWidth(animatedWidth.value)
            .clip(RoundedCornerShape(99.dp))
            .focusRequester(focusRequester)
            .clickable(onClick = onAdd),
                enabled = isExpanded,
//        todo: Find a way to pass "Add" string to value when !isExpanded and keep the right size and animation
//        value = if (isExpanded) inputValue  else stringResource(R.string.input_field_add),
        value = if (isExpanded) inputValue else "",
        textStyle = if (isExpanded) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyLarge,
        placeholder = {
            if (isExpanded) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )

            }
        },
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
                onClick = { onTrailingIconClick() }
            ) {
                Icon(
                    modifier = Modifier.rotate(
                        animatedRotation.value
                    ),
                    imageVector = Icons.Default.Clear,
                    contentDescription = if (isExpanded){
                        stringResource(R.string.input_field_delete)
                    } else {
                        stringResource(R.string.input_field_add)
                    }
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

