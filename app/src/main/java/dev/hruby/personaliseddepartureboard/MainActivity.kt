package dev.hruby.personaliseddepartureboard

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.hruby.personaliseddepartureboard.data.model.Profile
import dev.hruby.personaliseddepartureboard.data.model.Stop
import dev.hruby.personaliseddepartureboard.data.model.Validity
import dev.hruby.personaliseddepartureboard.ui.SearchDeparturesScreen
import dev.hruby.personaliseddepartureboard.ui.theme.PersonalisedDepartureBoardTheme
import java.time.LocalTime

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
    CreateProfile(title = R.string.create_profile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun DepartureBoardScreen(
    onSearchDeparturesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Button(onClick = onSearchDeparturesClick) {
            Text(text = "Search Departures")
        }
        ProfileCard(
            profile = Profile(
                "Work",
                Stop("U1Z1P", "Boletická", "A"),
                Validity(LocalTime.of(19, 0), LocalTime.of(20, 0))
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
fun ProfileCard(profile: Profile, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = profile.Name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Stop: ${profile.Stop.stopName}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Validity: ${profile.Validity.From} - ${profile.Validity.To}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DepartureBoardApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.DepartureBoard.name
    )

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
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
                    onSearchDeparturesClick = { navController.navigate(Screen.SearchDepartures.name) }
                )
            }
            composable(route = Screen.SearchDepartures.name) {
                SearchDeparturesScreen(
                    onCancelButtonClicked = { navController.navigate(Screen.DepartureBoard.name) }
                )
            }
        }
    }

}

