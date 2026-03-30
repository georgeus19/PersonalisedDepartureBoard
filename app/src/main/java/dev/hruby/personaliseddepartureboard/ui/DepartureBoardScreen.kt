package dev.hruby.personaliseddepartureboard.ui
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hruby.personaliseddepartureboard.data.model.Profile
import dev.hruby.personaliseddepartureboard.data.model.Stop
import dev.hruby.personaliseddepartureboard.data.model.Validity
import java.time.LocalTime

@Composable
fun DepartureBoardScreen(
    onSearchDeparturesClick: () -> Unit,
    departureBoardViewModel: DepartureBoardViewModel,
    modifier: Modifier = Modifier
) {
    val state by departureBoardViewModel.state.collectAsState()
    Column(modifier = modifier) {
        for (profile in state.profiles) {
            ProfileCard(profile = profile)
        }
    }
}

@Composable
fun ProfileCard(profile: Profile, modifier: Modifier = Modifier) {
    val stop = profile.stops.firstOrNull()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleLarge
            )

            stop?.let {
                Text(
                    text = "Stop: ${it.name}",
                    style = MaterialTheme.typography.bodyLarge
                )

                if (it.platforms.isNotEmpty()) {
                    ChipSection(
                        title = "Platforms",
                        values = it.platforms
                    )
                }

                if (it.lines.isNotEmpty()) {
                    ChipSection(
                        title = "Lines",
                        values = it.lines
                    )
                }
            }

            Text(
                text = "Validity: ${profile.validity.from} - ${profile.validity.to}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun ChipSection(
    title: String,
    values: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.sortedBy { it }.forEach { value ->
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(value)
                    }
                )
            }
        }
    }
}
