package com.ricdev.uread.presentation.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ricdev.uread.data.model.ReadingActivity
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar

@Composable
fun ReadingHeatmap(
    readingActivities: List<ReadingActivity>,
    modifier: Modifier = Modifier
) {
    val currentCalendar = Calendar.getInstance()
    val startOfYearCalendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val daysInWeek = 7
    val totalDays = ChronoUnit.DAYS.between(
        startOfYearCalendar.toInstant(),
        currentCalendar.toInstant()
    ).toInt() + 1
    val weeksToShow = totalDays / 7 + 1

    val sortedData = readingActivities.groupBy {
        Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    val calendar = startOfYearCalendar.clone() as Calendar

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .horizontalScroll(scrollState)
        ) {
            repeat(weeksToShow) {
                Column {
                    repeat(daysInWeek) {
                        val currentDate = calendar.timeInMillis
                        if (currentDate <= currentCalendar.timeInMillis) {
                            val currentLocalDate = Instant.ofEpochMilli(currentDate)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val readingData = sortedData[currentLocalDate] ?: emptyList()
                            val readingTime = readingData.sumOf { it.readingTime / 60000 } // Convert to minutes

                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(1.dp)
                                    .background(
                                        color = getColorForReadingTime(readingTime),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        } else {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
            }
        }
    }
}

@Composable
fun getColorForReadingTime(readingTimeMinutes: Long): Color {
    val baseColor = MaterialTheme.colorScheme.onSurface
    return when {
        readingTimeMinutes == 0L -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        readingTimeMinutes < 15 -> baseColor.copy(alpha = 0.2f)
        readingTimeMinutes < 30 -> baseColor.copy(alpha = 0.4f)
        readingTimeMinutes < 60 -> baseColor.copy(alpha = 0.6f)
        readingTimeMinutes < 120 -> baseColor.copy(alpha = 0.8f)
        else -> baseColor
    }
}