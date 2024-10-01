package com.ricdev.uread.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ricdev.uread.data.model.AppTheme


@Composable
fun SegmentedThemeControl(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ThemeOption(
            icon = Icons.Default.PhoneAndroid,
            label = "System",
            isSelected = selectedTheme == AppTheme.SYSTEM,
            onClick = { onThemeSelected(AppTheme.SYSTEM) }
        )
        ThemeOption(
            icon = Icons.Default.LightMode,
            label = "Light",
            isSelected = selectedTheme == AppTheme.LIGHT,
            onClick = { onThemeSelected(AppTheme.LIGHT) }
        )
        ThemeOption(
            icon = Icons.Default.DarkMode,
            label = "Dark",
            isSelected = selectedTheme == AppTheme.DARK,
            onClick = { onThemeSelected(AppTheme.DARK) }
        )
    }
}

@Composable
fun ThemeOption(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(label)
            }
        },
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}



//@Composable
//fun SegmentedButtons(
//    modifier: Modifier = Modifier,
//    shape: Shape = RoundedCornerShape(percent = 50),
//    colors: SegmentedButtonColors = SegmentedButtonsDefaults.colors(),
//    outlineThickness: Dp = SegmentedButtonsDefaults.outlineThickness,
//    border: BorderStroke = BorderStroke(outlineThickness, colors.outlineColor),
//    content: @Composable () -> Unit
//) {
//    Surface(
//        shape = shape,
//        border = border,
//        modifier = modifier.defaultMinSize(minHeight = 40.dp)
//    ) {
//        SubcomposeLayout(Modifier.fillMaxWidth()) { constraints ->
//            val buttonMeasurable = subcompose("Buttons", content)
//            val buttonCount = buttonMeasurable.size
//            val buttonWidth = constraints.maxWidth / buttonCount
//
//            val buttonPlaceable = buttonMeasurable.map {
//                it.measure(constraints.copy(minWidth = buttonWidth, maxWidth = buttonWidth))
//            }
//
//            layout(constraints.maxWidth, buttonPlaceable.maxOf { it.height }) {
//                buttonPlaceable.forEachIndexed { index, placeable ->
//                    placeable.placeRelative(index * buttonWidth, 0)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SegmentedButtonItem(
//    selected: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    colors: SegmentedButtonColors = SegmentedButtonsDefaults.colors(),
//    icon: @Composable (() -> Unit)? = null,
//    label: @Composable () -> Unit
//) {
//    val contentColor = colors.contentColor(selected, enabled)
//
//    Surface(
//        selected = selected,
//        onClick = onClick,
//        modifier = modifier.height(40.dp),
//        enabled = enabled,
//        color = colors.containerColor(selected, enabled)
//    ) {
//        Row(
//            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            CompositionLocalProvider(LocalContentColor provides contentColor) {
//                if (icon != null) {
//                    Box(Modifier.size(18.dp)) {
//                        icon()
//                    }
//                    Spacer(Modifier.width(8.dp))
//                }
//                label()
//            }
//        }
//    }
//}
//
//object SegmentedButtonsDefaults {
//    val outlineThickness: Dp = 1.dp
//
//    @Composable
//    fun colors(
//        containerColor: Color = MaterialTheme.colorScheme.surface,
//        contentColor: Color = MaterialTheme.colorScheme.onSurface,
//        selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
//        selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
//        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
//        disabledContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
//        outlineColor: Color = MaterialTheme.colorScheme.outline
//    ): SegmentedButtonColors = DefaultSegmentedButtonColors(
//        containerColor = containerColor,
//        contentColor = contentColor,
//        selectedContainerColor = selectedContainerColor,
//        selectedContentColor = selectedContentColor,
//        disabledContainerColor = disabledContainerColor,
//        disabledContentColor = disabledContentColor,
//        outlineColor = outlineColor
//    )
//}
//
//interface SegmentedButtonColors {
//    @Composable
//    fun containerColor(selected: Boolean, enabled: Boolean): Color
//    @Composable
//    fun contentColor(selected: Boolean, enabled: Boolean): Color
//    val outlineColor: Color
//}
//
//private class DefaultSegmentedButtonColors(
//    private val containerColor: Color,
//    private val contentColor: Color,
//    private val selectedContainerColor: Color,
//    private val selectedContentColor: Color,
//    private val disabledContainerColor: Color,
//    private val disabledContentColor: Color,
//    override val outlineColor: Color
//) : SegmentedButtonColors {
//    @Composable
//    override fun containerColor(selected: Boolean, enabled: Boolean): Color = when {
//        !enabled -> disabledContainerColor
//        selected -> selectedContainerColor
//        else -> containerColor
//    }
//
//    @Composable
//    override fun contentColor(selected: Boolean, enabled: Boolean): Color = when {
//        !enabled -> disabledContentColor
//        selected -> selectedContentColor
//        else -> contentColor
//    }
//}