package com.pobedie.ohmyping.screen.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pobedie.ohmyping.R


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBar(
    listenerEnabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .absoluteOffset(y = (-1).dp) // hack to hide border at the top of the screen
            .clip(RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp))
            .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.16f))
            .border(
                width = Dp.Hairline,
                color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f),
                shape = RoundedCornerShape(bottomEnd = 60.dp, bottomStart = 60.dp)
            ),
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
                  .background(MaterialTheme.colorScheme.tertiary)
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
