package dev.hruby.personaliseddepartureboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hruby.personaliseddepartureboard.data.model.DepartureFeature
import dev.hruby.personaliseddepartureboard.ui.theme.PersonalisedDepartureBoardTheme
import dev.hruby.personaliseddepartureboard.ui.viewmodel.DepartureViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalisedDepartureBoardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DepartureScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun DepartureScreen(
    modifier: Modifier = Modifier,
    viewModel: DepartureViewModel = viewModel()
) {
    val departures by viewModel.departures.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var stopNameInput by remember { mutableStateOf("Malostranské náměstí") }
    val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NDg1NCwiaWF0IjoxNzczNjAxNTI3LCJleHAiOjExNzczNjAxNTI3LCJpc3MiOiJnb2xlbWlvIiwianRpIjoiZjUxNTM2NjctMDU1Yy00NWQxLWE1YjctOTc5ZTBhNjg0YTk2In0.S2Csa_AZJe3qUCh_V_5nkZLTSScOM7yJEFMus3MinrY"

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = stopNameInput,
            onValueChange = { stopNameInput = it },
            label = { Text("Stop Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.fetchDeparturesByName(stopNameInput, apiKey) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search Departures")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(departures) { departure ->
                    DepartureItem(departure)
                }
            }
        }
    }
}

@Composable
fun DepartureItem(departure: DepartureFeature) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${departure.route.shortName} -> ${departure.trip.headsign}",
                    style = MaterialTheme.typography.titleMedium
                )
                departure.lastStop?.let {
                    Text(
                        text = "Last stop: ${it.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val time = (departure.departureTimestamp.predicted ?: departure.departureTimestamp.scheduled)
                Text(
                    text = if (time.length >= 16) time.substring(11, 16) else time,
                    style = MaterialTheme.typography.headlineSmall
                )
                departure.departureTimestamp.minutes?.let {
                    Text(
                        text = if (it == "<1") "now" else "$it min",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (departure.departureTimestamp.predicted != null)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
