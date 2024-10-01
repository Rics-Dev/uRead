package com.ricdev.uread.presentation.statistics


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ricdev.uread.R
import com.ricdev.uread.presentation.sharedComponents.CustomNavigationDrawer
import com.ricdev.uread.presentation.statistics.components.ReadingGraph
import com.ricdev.uread.presentation.statistics.components.ReadingHeatmap
import com.ricdev.uread.presentation.statistics.components.StatColumn
import com.ricdev.uread.util.PurchaseHelper
import kotlinx.coroutines.launch
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val statistics by viewModel.statistics.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)



    CustomNavigationDrawer(
        purchaseHelper = purchaseHelper,
        drawerState = drawerState,
        navController = navController
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.statistics)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {


                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.books),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            StatColumn(
                                title = "Total",
                                value = statistics.totalBooks.toString()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatColumn(
                                title = "Read",
                                value = statistics.booksRead.toString()
                            )
                            StatColumn(
                                title = "In Progress",
                                value = statistics.booksInProgress.toString()
                            )
                            StatColumn(
                                title = "To Read",
                                value = statistics.booksToRead.toString()
                            )
                        }
                    }
                }


                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.books_read),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            StatColumn(
                                title = "Total",
                                value = statistics.booksRead.toString()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatColumn(
                                title = "This Year",
                                value = statistics.booksReadThisYear.toString()
                            )
                            StatColumn(
                                title = "This Month",
                                value = statistics.booksReadThisMonth.toString()
                            )
                        }
                    }
                }





                // Average Rating Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.ratings),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatColumn(
                                title = "Rated Books",
                                value = statistics.ratedBooks.toString()
                            )
                            StatColumn(
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                title = "Average Rating",
                                value = String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    statistics.averageRating
                                )
                            )
                        }
                    }
                }




                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.annotations),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            StatColumn(
                                title = "Total",
                                value = (statistics.totalNotes + statistics.totalHighlights + statistics.totalUnderlines).toString()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatColumn(
                                title = "Notes",
                                value = statistics.totalNotes.toString()
                            )
                            StatColumn(
                                title = "Highlights",
                                value = statistics.totalHighlights.toString()
                            )
                            StatColumn(
                                title = "Underlines",
                                value = statistics.totalUnderlines.toString()
                            )
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.authors),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.favorite_authors),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(text = stringResource(R.string.author))
                            Text(text = stringResource(R.string.books))
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 2.dp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                statistics.favoriteAuthors.take(5).forEach { author ->
                                    Text(text = author.name, style = MaterialTheme.typography.bodyMedium, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                statistics.favoriteAuthors.take(5).forEach { author ->
                                    Text(text = author.books.size.toString(), style = MaterialTheme.typography.bodyMedium, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }

                    }
                }




                //Reading habits
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.reading_times),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.total_reading_time),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val totalTime = formatReadingTime(statistics.totalReadingTime)
                            val (totalHours, totalMinutes) = totalTime.split(" h ", " min")
                            StatColumn(
                                title = "Hours",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = totalHours
                            )
                            StatColumn(
                                title = "Minutes",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = totalMinutes
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.average_reading_time_per_book),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val averageTime =
                                formatReadingTime(statistics.averageReadingTimePerBook)
                            val (averageHours, averageMinutes) = averageTime.split(" h ", " min")
                            StatColumn(
                                title = "Hours",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = averageHours
                            )
                            StatColumn(
                                title = "Minutes",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = averageMinutes
                            )
                        }


                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.daily_reading_time),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val dailyTime = formatReadingTime(statistics.averageDailyReadingTime)
                            val (dailyHours, dailyMinutes) = dailyTime.split(" h ", " min")
                            StatColumn(
                                title = "Hours",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = dailyHours
                            )
                            StatColumn(
                                title = "Minutes",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = dailyMinutes
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.reading_graph),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        ReadingGraph(readingActivities = statistics.readingActivities)

                    }
                }



//                //Reading heatmap
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.reading_habits),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatColumn(
                                title = "Longest Streak",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = "${statistics.longestReadingStreak} day${if (statistics.longestReadingStreak > 1) "s" else ""}"
                            )
                            StatColumn(
                                title = "Current Streak",
                                titleStyle = MaterialTheme.typography.bodyMedium,
                                value = "${statistics.currentReadingStreak} day${if (statistics.currentReadingStreak > 1) "s" else ""}"
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ReadingHeatmap(readingActivities = statistics.readingActivities)
                    }
                }
            }
        }
    }
}











fun formatReadingTime(timeInMillis: Long): String {
    val hours = timeInMillis / (1000 * 60 * 60)
    val minutes = (timeInMillis % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (timeInMillis % (1000 * 60)) / 1000
    return String.format(Locale.getDefault(), "%d h %d min %d s", hours, minutes, seconds)
}