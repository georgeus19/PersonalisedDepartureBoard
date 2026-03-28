package dev.hruby.personaliseddepartureboard.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hruby.personaliseddepartureboard.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun CreateProfileScreen(
    viewModel: DepartureBoardViewModel,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        viewModel.startProfileCreate()
    }

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

        Column() {
            for (stop in state.editedProfile!!.stops) {
                Text(text = stop.name)
            }
        }

        CancelNextButtons(
            onNext = onNext,
            onCancel = onCancel
        )
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun CreateProfileScreenPreview() {
    CreateProfileScreen(
        DepartureBoardViewModel(),
        onNext = {},
        onCancel = {},
    )
}
