package org.quicksc0p3r.simplecounter.ui.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.CountersViewModel
import org.quicksc0p3r.simplecounter.db.Label
import org.quicksc0p3r.simplecounter.db.LabelsViewModel

@Composable
fun DeleteDialog(
    dismiss: () -> Unit,
    isLabel: Boolean,
    countersViewModel: CountersViewModel,
    labelsViewModel: LabelsViewModel? = null,
    counter: Counter? = null,
    deleteCounter: Boolean = true,
    label: Label? = null,
    clearLabelFilter: (() -> Unit)? = null,
    beforeDelete: (() -> Unit)? = null
) {
    val counters by countersViewModel.allCounters.observeAsState(listOf())

    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(text = if (!isLabel) stringResource(R.string.delete_counter, counter!!.name) else stringResource(R.string.delete_label, label!!.name)) },
        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = stringResource(R.string.delete)) },
        text = {
            if (isLabel)
                Text(text = stringResource(R.string.delete_label_warning))
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (beforeDelete != null) beforeDelete()
                    if (!isLabel && deleteCounter)
                        countersViewModel.deleteCounter(counter!!)
                    else {
                        counters.forEach {counter ->
                            if (counter.labelId == label!!.id)
                                countersViewModel.updateCounter2(
                                    Counter(
                                        id = counter.id,
                                        name = counter.name,
                                        value = counter.value,
                                        defaultValue = counter.defaultValue,
                                        labelId = null,
                                        allowNegativeValues = counter.allowNegativeValues
                                    )
                                )
                        }
                        clearLabelFilter!!()
                        labelsViewModel!!.deleteLabel(label!!)
                    }
                    dismiss()

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = stringResource(R.string.delete))
            }
        }
    )
}