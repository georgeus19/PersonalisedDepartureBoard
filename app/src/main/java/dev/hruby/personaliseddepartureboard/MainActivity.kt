package dev.hruby.personaliseddepartureboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.hruby.personaliseddepartureboard.data.model.LastStop
import dev.hruby.personaliseddepartureboard.ui.AppBar
import dev.hruby.personaliseddepartureboard.ui.CreateProfileScreen
import dev.hruby.personaliseddepartureboard.ui.DepartureBoardScreen
import dev.hruby.personaliseddepartureboard.ui.DepartureBoardViewModel
import dev.hruby.personaliseddepartureboard.ui.Line
import dev.hruby.personaliseddepartureboard.ui.MultiSelectScreen
import dev.hruby.personaliseddepartureboard.ui.Platform
import dev.hruby.personaliseddepartureboard.ui.SearchDeparturesScreen
import dev.hruby.personaliseddepartureboard.ui.SelectStopScreen
import dev.hruby.personaliseddepartureboard.ui.SelectableItem
import dev.hruby.personaliseddepartureboard.ui.theme.PersonalisedDepartureBoardTheme
import kotlin.collections.forEach

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalisedDepartureBoardTheme {
                DepartureBoardApp()
            }
        }
    }
}

enum class Screen(@StringRes val title: Int) {
    SearchDepartures(title = R.string.search_departures),
    DepartureBoard(title = R.string.departure_board),
    CreateProfile(title = R.string.create_profile),
    SelectStop(title = R.string.select_stop),
    SelectLines(title = R.string.select_lines),
    SelectPlatforms(title = R.string.select_platforms)

}

@Composable
fun DepartureBoardApp(
    navController: NavHostController = rememberNavController(),
    departureBoardViewModel: DepartureBoardViewModel = viewModel()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = try {
        Screen.valueOf(backStackEntry?.destination?.route ?: Screen.DepartureBoard.name)
    } catch (e: Exception) {
        Screen.DepartureBoard
    }

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                actions = {
                    if (currentScreen == Screen.DepartureBoard) {
                        IconButton(onClick = {
                            navController.navigate(Screen.SearchDepartures.name) {
                                launchSingleTop = true
                            }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_search_24),
                                contentDescription = stringResource(R.string.search_departures)
                            )
                        }

                        IconButton(onClick = {
                            departureBoardViewModel.startProfileCreate()
                            navController.navigate(Screen.CreateProfile.name) {
                                launchSingleTop = true
                            }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_add_24),
                                contentDescription = stringResource(R.string.create_profile)
                            )
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.DepartureBoard.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.DepartureBoard.name) {
                DepartureBoardScreen(
                    onSearchDeparturesClick = { navController.navigate(Screen.SearchDepartures.name) },
                    departureBoardViewModel = departureBoardViewModel,
                    modifier = Modifier.padding(8.dp)
                )
            }
            composable(route = Screen.SearchDepartures.name) {
                SearchDeparturesScreen(
                    viewModel = departureBoardViewModel,
                    onCancelButtonClicked = { navController.navigate(Screen.DepartureBoard.name) },
                    modifier = Modifier.padding(8.dp)
                )
            }
            composable(route = Screen.CreateProfile.name) {
                CreateProfileScreen(
                    viewModel = departureBoardViewModel,
                    onAddStop = {
                        departureBoardViewModel.startProfileStopEdit()
                        navController.navigate(Screen.SelectStop.name)
                    },
                    onNext = {
                        departureBoardViewModel.startProfileStopEdit()
                        navController.navigate(Screen.SelectStop.name)
                    },
                    onCancel = {
                        departureBoardViewModel.cancelProfileEdit()
                        navController.navigate(Screen.DepartureBoard.name)
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
            composable(route = Screen.SelectStop.name) {
                SelectStopScreen(
                    viewModel = departureBoardViewModel,
                    onNext = {
                        navController.navigate(Screen.SelectLines.name)
                    },
                    onCancel = {
                        departureBoardViewModel.cancelProfileEdit()
                        navController.navigate(Screen.DepartureBoard.name)
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
            composable(route = Screen.SelectLines.name) {
                val state by departureBoardViewModel.state.collectAsState()
                val lines by departureBoardViewModel.lines.collectAsState()
                val stopName = state.editedProfile?.stopDraft?.name
                LaunchedEffect(stopName) {
                    stopName?.let { departureBoardViewModel.fetchLines(it) }
                }
                MultiSelectScreen(
                    items = lines.map { line ->
                        SelectableItem(
                            id = line.id,
                            cardContent = { LineOption(lineId = line.id) }
                        )
                    },
                    onNext = { lines ->
                        departureBoardViewModel.updateDraftStopLine(lines)
                        navController.navigate(Screen.SelectPlatforms.name)
                    },
                    onCancel = {
                        departureBoardViewModel.cancelProfileEdit()
                        navController.navigate(Screen.DepartureBoard.name)
                    },
                    initiallySelectedIds = state.editedProfile?.stopDraft?.lines?.toSet() ?: emptySet(),
                    modifier = Modifier.padding(8.dp)
                )
            }
            composable(route = Screen.SelectPlatforms.name) {

                val state by departureBoardViewModel.state.collectAsState()
                val platforms by departureBoardViewModel.platforms.collectAsState()
                val stopName = state.editedProfile?.stopDraft?.name
                LaunchedEffect(stopName) {
                    stopName?.let { departureBoardViewModel.fetchPlatforms(it) }
                }
                MultiSelectScreen(
                    items = platforms.map {
                        SelectableItem(id = it.code, cardContent = { PlatformOption(it) })
                    },
                    onNext = { platforms: List<String> ->
                        departureBoardViewModel.updateDraftStopPlatform(platforms)
                        departureBoardViewModel.createProfileStop()
                        navController.navigate(Screen.CreateProfile.name)
                    },
                    onCancel = {
                        departureBoardViewModel.cancelProfileEdit()

                        navController.navigate(Screen.DepartureBoard.name)
                    },
                    initiallySelectedIds = emptySet(),
                    modifier = Modifier.padding(8.dp)
                )
            }

        }
    }

}

@Composable
fun LineOption(
    lineId: String,
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = lineId)
    }
}

@Composable
fun PlatformOption(
    platform: Platform
) {
    Card(
        modifier = Modifier.padding(8.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = platform.code,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                platform.lines.forEach { line ->
                    AssistChip(
                        onClick = {},

                        enabled = false,
                        label = {
                            Row() {
                                Text(line.id)
                                Text(" -> ")
                                Text(line.lastStop ?: "")
                            }
                        }
                    )
                }
            }
        }
    }
}
