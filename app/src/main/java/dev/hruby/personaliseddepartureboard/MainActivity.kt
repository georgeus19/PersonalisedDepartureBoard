package dev.hruby.personaliseddepartureboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.hruby.personaliseddepartureboard.ui.AppBar
import dev.hruby.personaliseddepartureboard.ui.CreateProfileScreen
import dev.hruby.personaliseddepartureboard.ui.DepartureBoardScreen
import dev.hruby.personaliseddepartureboard.ui.DepartureBoardViewModel
import dev.hruby.personaliseddepartureboard.ui.MultiSelectScreen
import dev.hruby.personaliseddepartureboard.ui.SearchDeparturesScreen
import dev.hruby.personaliseddepartureboard.ui.SelectStopScreen
import dev.hruby.personaliseddepartureboard.ui.SelectableItem
import dev.hruby.personaliseddepartureboard.ui.theme.PersonalisedDepartureBoardTheme

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
                    departureBoardViewModel = departureBoardViewModel
                )
            }
            composable(route = Screen.SearchDepartures.name) {
                SearchDeparturesScreen(
                    onCancelButtonClicked = { navController.navigate(Screen.DepartureBoard.name) }
                )
            }
            composable(route = Screen.CreateProfile.name) {
                CreateProfileScreen(
                    viewModel = departureBoardViewModel,
                    onNext = {
                        departureBoardViewModel.startProfileStopEdit()
                        navController.navigate(Screen.SelectStop.name)
                    },
                    onCancel = {
                        departureBoardViewModel.cancelProfileEdit()
                        navController.navigate(Screen.DepartureBoard.name)
                    }
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
                )
            }
            composable(route = Screen.SelectLines.name) {
                val state by departureBoardViewModel.state.collectAsState()
                MultiSelectScreen(
                    items = listOf(
                        SelectableItem(id = "1", cardContent = { LineOption("1") }),
                        SelectableItem(id = "2", cardContent = { LineOption("2") })
                    ),
                    onNext = { lines ->
                        departureBoardViewModel.updateDraftStopLine(lines)
                        navController.navigate(Screen.SelectPlatforms.name)
                    },
                    onCancel = {
                        departureBoardViewModel.cancelProfileEdit()
                        navController.navigate(Screen.DepartureBoard.name)
                    },
                    initiallySelectedIds = state.editedProfile?.stopDraft?.lines?.toSet() ?: emptySet()
                )
            }
            composable(route = Screen.SelectPlatforms.name) {
                MultiSelectScreen(
                    items = listOf(
                        SelectableItem(id = "A", cardContent = { PlatformOption("A") }),
                        SelectableItem(id = "B", cardContent = { PlatformOption("B") }),
                    ),
                    onNext = { platforms: List<String> ->
                        departureBoardViewModel.updateDraftStopPlatform(platforms)
                        navController.navigate(Screen.CreateProfile.name)
                    },
                    onCancel = {
                        departureBoardViewModel.cancelProfileEdit()
                        navController.navigate(Screen.DepartureBoard.name)
                    },
                    initiallySelectedIds = emptySet()
                )
            }

        }
    }

}

@Composable
fun LineOption(
    text: String
) {
    Text(text = text)
}

@Composable
fun PlatformOption(
    text: String
) {
    Text(text = text)
}
