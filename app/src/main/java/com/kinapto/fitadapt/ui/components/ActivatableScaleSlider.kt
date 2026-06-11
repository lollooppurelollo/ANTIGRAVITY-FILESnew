package com.kinapto.fitadapt.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivatableScaleSlider(
    label: String,
    value: Int,
    isActive: Boolean,
    onValueChange: (Int) -> Unit,
    onActivated: () -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 0..10,
    minLabel: String,
    maxLabel: String
) {
    val trackColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "trackColor"
    )

    val thumbColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "thumbColor"
    )

    val thumbBorderColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "thumbBorderColor"
    )

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Surface(
                color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isActive) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "— non risposto",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { 
                if (!isActive) onActivated()
                onValueChange(it.toInt()) 
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1,
            colors = SliderDefaults.colors(
                activeTrackColor = trackColor,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(thumbColor, CircleShape)
                        .border(2.dp, thumbBorderColor, CircleShape)
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = minLabel, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = maxLabel, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (!isActive) {
            Text(
                text = "Scorri per rispondere",
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
