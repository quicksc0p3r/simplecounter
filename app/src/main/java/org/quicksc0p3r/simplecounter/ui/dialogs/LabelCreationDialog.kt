package org.quicksc0p3r.simplecounter.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Label
import org.quicksc0p3r.simplecounter.db.LabelsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelCreationDialog(dismiss: () -> Unit, viewModel: LabelsViewModel) {
    var labelName by remember { mutableStateOf("") }
    var labelNameIsNotEmpty by remember { mutableStateOf(false) }
    var labelColor by remember { mutableStateOf(0xFFE15241) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val metrics = context.resources.displayMetrics
    val dpWidth = metrics.widthPixels / metrics.density
    val colors = listOf(
        0xFFD63864,
        0xFFE15241,
        0xFFF19D38,
        0xFFF6C343,
        0xFFD0DC59,
        0xFF97C15C,
        0xFF429588,
        0xFF54B9D1,
        0xFF4BA6EE,
        0xFF4350AF,
        0xFF603CB0,
        0xFF74574A,
        0xFF9E9E9E,
        0xFF667C89
    )
    val splitColors = colors.chunked(if (dpWidth >= 380) 7 else 4)

    fun checkIfNotEmpty() {
        labelNameIsNotEmpty = labelName.isNotEmpty()
    }

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
            Text(text = stringResource(R.string.create_label), style = MaterialTheme.typography.headlineSmall)
            TextField(
                modifier = Modifier.fillMaxWidth(0.95F).padding(vertical = 10.dp),
                value = labelName,
                onValueChange = {labelName = it; checkIfNotEmpty()},
                label = { Text(text = stringResource(R.string.name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                singleLine = true
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                splitColors.forEach { colorGroup ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        colorGroup.forEach { color ->
                            FilledIconButton(
                                onClick = {labelColor = color},
                                content = {
                                    if (labelColor == color)
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null
                                        )
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color(color),
                                    contentColor = Color(0xFFFFFFFF)
                                )
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {dismiss()},
                        content = { Text(text = stringResource(R.string.cancel)) }
                    )
                    TextButton(
                        enabled = labelNameIsNotEmpty,
                        onClick = {
                            viewModel.insertLabel(Label(id = 0, name = labelName, color = labelColor))
                            dismiss()
                        },
                        content = { Text(text = stringResource(R.string.create)) }
                    )
                }
            }
        }
    }
}