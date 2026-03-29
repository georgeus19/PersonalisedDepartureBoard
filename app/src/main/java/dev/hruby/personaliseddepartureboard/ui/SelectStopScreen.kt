package dev.hruby.personaliseddepartureboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.hruby.personaliseddepartureboard.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SelectStopScreen(
    viewModel: DepartureBoardViewModel,
    onCancel: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val initialName = state.editedProfile?.stopDraft?.name ?: ""
    
    var stopText by remember(initialName) {
        mutableStateOf(initialName)
    }
    
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = stopText,
            label = { Text(text = stringResource(R.string.stop_name)) },
            onValueChange = { stopText = it },
        )
        CancelNextButtons(
            onCancel = onCancel,
            onNext = {
                viewModel.updateDraftStopName(stopText)
                onNext()
            },
        )
    }
}
