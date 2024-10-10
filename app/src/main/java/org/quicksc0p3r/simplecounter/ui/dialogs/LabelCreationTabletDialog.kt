package org.quicksc0p3r.simplecounter.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Label
import org.quicksc0p3r.simplecounter.db.LabelsViewModel

@Composable
fun LabelCreationTabletDialog(dismiss: () -> Unit, viewModel: LabelsViewModel) {
    var labelName by rememberSaveable { mutableStateOf("") }
    var labelNameIsNotEmpty by rememberSaveable { mutableStateOf(false) }
    var labelColor by rememberSaveable { mutableStateOf(0xFFE15241) }
    val colors1 = listOf(
        0xFFD63864,
        0xFFE15241,
        0xFFF19D38,
        0xFFF6C343,
        0xFFD0DC59,
        0xFF97C15C,
        0xFF429588
    )
    val colors2 = listOf(
        0xFF54B9D1,
        0xFF4BA6EE,
        0xFF4350AF,
        0xFF603CB0,
        0xFF74574A,
        0xFF9E9E9E,
        0xFF667C89
    )

    fun checkIfNotEmpty() {
        labelNameIsNotEmpty = labelName.isNotEmpty()
    }

    AlertDialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = { Text(text = stringResource(R.string.create_label)) },
        text = {
            val focusManager = LocalFocusManager.current

            Column(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.width(330.dp),
                    value = labelName,
                    onValueChange = {labelName = it; checkIfNotEmpty()},
                    label = { Text(text = stringResource(R.string.name)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                    singleLine = true
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        colors1.forEach {color ->
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        colors2.forEach {color ->
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
                                    contentColor = Color(0xFFFFFFFF),
                                    disabledContainerColor = Color(0xFF808080),
                                    disabledContentColor = Color(0xFFFFFFFF)
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = labelNameIsNotEmpty,
                onClick = {
                    viewModel.insertLabel(Label(id = 0, name = labelName, color = labelColor))
                    dismiss()
                },
                content = { Text(text = stringResource(R.string.create)) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = {dismiss()},
                content = { Text(text = stringResource(R.string.cancel)) }
            )
        }
    )
}