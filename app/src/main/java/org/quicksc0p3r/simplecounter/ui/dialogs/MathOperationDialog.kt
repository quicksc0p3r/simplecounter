package org.quicksc0p3r.simplecounter.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.evaluateMathOperation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathOperationDialog(dismiss: () -> Unit, counterValue: Int, minValue: Int, maxValue: Int, updateCounter: (Int) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var operationString by remember { mutableStateOf("") }
    var mathOperationValue: Int? by remember { mutableStateOf(null) }

    ModalBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState,
        windowInsets = BottomSheetDefaults.windowInsets
    ) {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 5.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.math_operation), style = MaterialTheme.typography.headlineSmall)
            TextField(
                modifier = Modifier
                    .fillMaxWidth(0.95F)
                    .padding(vertical = 10.dp),
                value = operationString,
                onValueChange = { operationString = it; mathOperationValue = evaluateMathOperation(counterValue, operationString, minValue, maxValue) },
                placeholder = { Text(text = stringResource(R.string.math_operation_hint)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                singleLine = true
            )
            Text(
                text = stringResource(R.string.math_operation_round_notice),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 5.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = dismiss,
                    content = { Text(text = stringResource(R.string.cancel)) }
                )
                TextButton(
                    enabled = mathOperationValue != null,
                    onClick = {
                        mathOperationValue?.let { updateCounter(it) }
                        dismiss()
                    },
                    content = { Text(text = stringResource(R.string.done)) }
                )
            }
        }
    }
}