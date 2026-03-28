package dev.hruby.personaliseddepartureboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.hruby.personaliseddepartureboard.R
import dev.hruby.personaliseddepartureboard.data.model.ProfileStopDraft
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SelectStopScreen(
    viewModel: DepartureBoardViewModel,
    onCancel: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val stopText = state.editedProfile!!.stopDraft!!.name ?: ""
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = stopText,
            label = { Text(text = stringResource(R.string.stop_name)) },
            onValueChange = { },
        )
        CancelNextButtons(
            onCancel = onCancel,
            onNext = {
                viewModel.updateDraftStopName(stopText);
                onNext()
            },
        )
    }


}