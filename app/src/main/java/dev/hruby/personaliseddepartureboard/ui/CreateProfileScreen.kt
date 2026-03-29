package dev.hruby.personaliseddepartureboard.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hruby.personaliseddepartureboard.R
import dev.hruby.personaliseddepartureboard.data.model.ProfileStop
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun CreateProfileScreen(
    viewModel: DepartureBoardViewModel,
    onAddStop: () -> Unit,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsState()

    if (state.editedProfile == null) {
        return
    }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = state.editedProfile!!.name,
            onValueChange = { viewModel.updateDraftName(it) },
            label = { Text(text = stringResource(R.string.select_profile_name), style = MaterialTheme.typography.bodyLarge) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        ValidityRangeSlider(
            from = state.editedProfile!!.from,
            to = state.editedProfile!!.to,
            onFromChanged = { viewModel.updateDraftFrom(it) },
            onToChanged = { viewModel.updateDraftTo(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Stops",
                style = MaterialTheme.typography.labelLarge
            )
            Button(
                onClick = onAddStop
            ) {
                Text(text = "Add stop")
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (stop in state.editedProfile!!.stops) {
                ProfileStopCard(stop)
            }
        }

        CancelNextButtons(
            onNext = onNext,
            onCancel = onCancel
        )
    }

}

@Composable
fun ProfileStopCard(
    stop: ProfileStop,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stop.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (stop.platforms.isNotEmpty()) {
                StopChipSection(
                    title = "Platforms",
                    values = stop.platforms
                )
            }

            if (stop.lines.isNotEmpty()) {
                HorizontalDivider()
                StopChipSection(
                    title = "Lines",
                    values = stop.lines
                )
            }
        }
    }
}

@Composable
private fun StopChipSection(
    title: String,
    values: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
            values
                .sortedBy { it }
                .forEach { value ->
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

@Composable
fun ValidityRangeSlider(
    from: LocalTime?,
    to: LocalTime?,
    onFromChanged: (LocalTime) -> Unit,
    onToChanged: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val startMinutes = from?.toMinutes()?.toFloat() ?: (8 * 60f)
    val endMinutes = to?.toMinutes()?.toFloat() ?: (16 * 60f)

    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Validity",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = "${minutesToLocalTime(startMinutes.roundToInt()).format(formatter)} - " +
                        "${minutesToLocalTime(endMinutes.roundToInt()).format(formatter)}"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        RangeSlider(
            value = startMinutes..endMinutes,
            onValueChange = { range ->
                val snappedFrom = snapToQuarterHour(range.start)
                val snappedTo = snapToQuarterHour(range.endInclusive)

                if (snappedFrom < snappedTo) {
                    onFromChanged(minutesToLocalTime(snappedFrom))
                    onToChanged(minutesToLocalTime(snappedTo))
                }
            },
            valueRange = 0f..1439f
        )

    }
}

private fun snapToQuarterHour(value: Float): Int {
    val minutes = value.roundToInt()
    val snapped = (minutes / 15.0).roundToInt() * 15
    return snapped.coerceIn(0, 23 * 60 + 45)
}

private fun minutesToLocalTime(totalMinutes: Int): LocalTime =
    LocalTime.of(totalMinutes / 60, totalMinutes % 60)

private fun LocalTime.toMinutes(): Int =
    hour * 60 + minute

//@SuppressLint("ViewModelConstructorInComposable")
//@Preview
//@Composable
//fun CreateProfileScreenPreview() {
//    CreateProfileScreen(
//        DepartureBoardViewModel(AD),
//        onNext = {},
//        onCancel = {},
//        onAddStop = {}
//    )
//}
