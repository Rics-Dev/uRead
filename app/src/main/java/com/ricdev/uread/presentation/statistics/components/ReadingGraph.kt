package com.ricdev.uread.presentation.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ricdev.uread.data.model.ReadingActivity
import java.util.Calendar

@Composable
fun ReadingGraph(
    readingActivities: List<ReadingActivity>
) {
    val calendar = Calendar.getInstance()
    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Adjust to 0-based index

    var selectedDayIndex by remember { mutableIntStateOf(currentDayOfWeek) }

    // Calculate the start day (6 days ago)
    val startCalendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -6)
    }

    // Create a list of 7 days ending with the current day
    val daysOfWeek = (0..6).map { dayOffset ->
        val dayCalendar = (startCalendar.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
        }
        val readingActivityForDay = readingActivities.find {
            val activityCalendar = Calendar.getInstance().apply {
                timeInMillis = it.date
            }
            activityCalendar.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR) &&
                    activityCalendar.get(Calendar.DAY_OF_YEAR) == dayCalendar.get(Calendar.DAY_OF_YEAR)
                    && it.readingTime >= 60000L
        }
        Pair(dayCalendar, readingActivityForDay?.readingTime ?: 0L)
    }

    val maxReadingTime = daysOfWeek.maxOfOrNull { it.second } ?: 0L

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = formatDate(daysOfWeek[selectedDayIndex].first), style = MaterialTheme.typography.bodyLarge)
        Text(text = formatReadingTime(daysOfWeek[selectedDayIndex].second))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            daysOfWeek.forEachIndexed { index, (dayCalendar, readingTime) ->
                val heightFactor =
                    if (maxReadingTime > 0) readingTime.toFloat() / maxReadingTime else 0f
                val dayName = when (dayCalendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SUNDAY -> "Sun"
                    Calendar.MONDAY -> "Mon"
                    Calendar.TUESDAY -> "Tue"
                    Calendar.WEDNESDAY -> "Wed"
                    Calendar.THURSDAY -> "Thu"
                    Calendar.FRIDAY -> "Fri"
                    Calendar.SATURDAY -> "Sat"
                    else -> ""
                }
                val isSelected = index == selectedDayIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .weight(1f)

                ) {
                    Box(
                        modifier = Modifier
                            .height((heightFactor * 100).dp)
                            .width(24.dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary
                            )
                            .clickable {
                                selectedDayIndex = index
                            }
                    )
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = dayName, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}



fun formatReadingTime(readingTime: Long): String {
    val minutes = (readingTime / 1000 / 60) % 60
    val hours = readingTime / 1000 / 60 / 60
    return "$hours h $minutes min"
}

fun formatDate(calendar: Calendar): String {
    val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sun"
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> ""
    }
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val month = when (calendar.get(Calendar.MONTH)) {
        Calendar.JANUARY -> "January"
        Calendar.FEBRUARY -> "February"
        Calendar.MARCH -> "March"
        Calendar.APRIL -> "April"
        Calendar.MAY -> "May"
        Calendar.JUNE -> "June"
        Calendar.JULY -> "July"
        Calendar.AUGUST -> "August"
        Calendar.SEPTEMBER -> "September"
        Calendar.OCTOBER -> "October"
        Calendar.NOVEMBER -> "November"
        Calendar.DECEMBER -> "December"
        else -> ""
    }
    return "$dayOfWeek. $dayOfMonth $month"
}
