package dev.hruby.personaliseddepartureboard.ui

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
    Column(modifier = modifier) {
//        ProfileCard(
//            profile = Profile(
//                "123",
//                "Work",
//                Stop("U1Z1P", "Boletická", "A"),
//                Validity(LocalTime.of(19, 0), LocalTime.of(20, 0))
//            ),
//            modifier = Modifier.padding(16.dp)
//        )
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
                text = profile.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Stop: ${profile.stops.first().name}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Validity: ${profile.validity.from} - ${profile.validity.to}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
