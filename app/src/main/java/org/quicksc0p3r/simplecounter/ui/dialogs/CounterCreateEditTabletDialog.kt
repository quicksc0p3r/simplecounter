package org.quicksc0p3r.simplecounter.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.quicksc0p3r.simplecounter.MAX_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.MIN_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.CountersViewModel
import org.quicksc0p3r.simplecounter.db.LabelsViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CounterCreateEditTabletDialog(dismiss: () -> Unit, countersViewModel: CountersViewModel, labelsViewModel: LabelsViewModel, isEdit: Boolean, counter: Counter? = null) {
    var counterName by rememberSaveable { mutableStateOf(if (isEdit) counter!!.name else "") }
    var counterDefaultValue by rememberSaveable { mutableStateOf(if (isEdit) counter!!.defaultValue.toString() else "") }
    var counterDefaultIntValue = counterDefaultValue.toIntOrNull()
    var counterValue by rememberSaveable { mutableStateOf(if (isEdit) counter!!.value.toString() else "") }
    var counterIntValue = counterValue.toIntOrNull()
    var counterLabel =
        if (isEdit) {
            if (counter!!.labelId != null)
                labelsViewModel.getLabelById(counter.labelId!!).collectAsState(initial = null).value
            else null
        } else null
    var counterNameIsNotEmpty by rememberSaveable { mutableStateOf(isEdit) }
    var counterValueIsValid by rememberSaveable { mutableStateOf(true) }
    var counterDefaultValueIsValid by rememberSaveable { mutableStateOf(true) }
    var allowNegativeValues by rememberSaveable { mutableStateOf(if (isEdit) counter!!.allowNegativeValues else false) }
    var labelDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    val labels by labelsViewModel.allLabels.observeAsState(listOf())
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics

    fun checkIfNotEmpty() {
        counterNameIsNotEmpty = counterName.isNotEmpty()
    }

    fun checkIfCounterValueIsValid() {
        if (counterValue.isEmpty()) {
            counterValueIsValid = true
        }
        else if (counterIntValue != null) {
            if (counterIntValue!! <= MAX_COUNTER_VALUE) {
                if (allowNegativeValues && (counterIntValue!! >= MIN_COUNTER_VALUE))
                    counterValueIsValid = true
                else if (counterIntValue!! >= 0)
                    counterValueIsValid = true
                else counterValueIsValid = false
            }
            else counterValueIsValid = false
        }
        else counterValueIsValid = false
    }

    fun checkIfCounterDefaultValueIsValid() {
        if (counterDefaultValue.isEmpty()) {
            counterDefaultValueIsValid = true
        }
        else if (counterDefaultIntValue != null) {
            if (counterDefaultIntValue!! <= MAX_COUNTER_VALUE) {
                if (allowNegativeValues && (counterDefaultIntValue!! >= MIN_COUNTER_VALUE))
                    counterDefaultValueIsValid = true
                else if (counterDefaultIntValue!! >= 0)
                    counterDefaultValueIsValid = true
                else counterDefaultValueIsValid = false
            }
            else counterDefaultValueIsValid = false
        }
        else counterDefaultValueIsValid = false
    }

    AlertDialog(
        modifier = Modifier
            .width(
                if (displayMetrics.widthPixels * displayMetrics.density >= 356) 320.dp
                else (displayMetrics.widthPixels * displayMetrics.density * 0.9).dp
            )
            .heightIn(
                max =
                if (displayMetrics.heightPixels * displayMetrics.density <= 480)
                    (displayMetrics.heightPixels * displayMetrics.density * 0.8).dp
                else Dp.Unspecified
            ),
        onDismissRequest = dismiss,
        title = { Text(text = if (isEdit) stringResource(R.string.edit_counter) else ContextCompat.getString(context, R.string.create_counter)) },
        confirmButton = {
            TextButton(
                enabled = counterNameIsNotEmpty && counterValueIsValid && counterDefaultValueIsValid,
                onClick = {
                    if (isEdit)
                        countersViewModel.updateCounter2(
                            Counter(
                            id = counter!!.id,
                            name = counterName,
                            defaultValue = if (counterDefaultValue.isNotEmpty()) counterDefaultValue.toInt() else 0,
                            labelId = if (counterLabel != null) counterLabel!!.id else null,
                            value = if (counterValue.isNotEmpty()) counterValue.toInt() else 0,
                            allowNegativeValues = allowNegativeValues
                        )
                        )
                    else
                        countersViewModel.insertCounter(
                            Counter(
                            id = 0,
                            name = counterName,
                            defaultValue = if (counterDefaultValue.isNotEmpty()) counterDefaultValue.toInt() else 0,
                            labelId = if (counterLabel != null) counterLabel!!.id else null,
                            value = if (counterDefaultValue.isNotEmpty()) counterDefaultValue.toInt() else 0,
                            allowNegativeValues = allowNegativeValues
                        )
                        )
                    dismiss()
                },
                content = { Text(text = if (isEdit) stringResource(R.string.save) else ContextCompat.getString(context, R.string.create)) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = dismiss,
                content = { Text(text = stringResource(R.string.cancel)) }
            )
        },
        text = {
            val focusManager = LocalFocusManager.current
            val (nameFieldFocus, defaultValueFieldFocus, valueFieldFocus) = remember{ FocusRequester.createRefs()}
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .focusRequester(nameFieldFocus)
                        .focusProperties { next = defaultValueFieldFocus },
                    value = counterName,
                    onValueChange = {counterName = it; checkIfNotEmpty()},
                    label = { Text(text = stringResource(R.string.name)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(
                        FocusDirection.Next)}),
                    singleLine = true
                )
                TextField(
                    modifier = Modifier
                        .focusRequester(defaultValueFieldFocus)
                        .focusProperties {
                            previous = nameFieldFocus
                            if (isEdit) next = valueFieldFocus
                        },
                    value = counterDefaultValue,
                    onValueChange = {
                        counterDefaultValue = it.filterIndexed { i, char ->
                            char.isDigit() || ((char == '-') && (i == 0))
                        }
                        counterDefaultIntValue = counterDefaultValue.toIntOrNull()
                        checkIfCounterDefaultValueIsValid()
                    },
                    label = { Text(text = stringResource(R.string.default_value)) },
                    placeholder = { Text(text = stringResource(R.string.zero_by_default)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = if (isEdit) ImeAction.Next else ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) },
                        onDone = { focusManager.clearFocus() }
                    ),
                    singleLine = true
                )
                if (isEdit)
                    TextField(
                        modifier = Modifier
                            .focusRequester(valueFieldFocus)
                            .focusProperties { previous = defaultValueFieldFocus },
                        value = counterValue,
                        onValueChange = {
                            counterValue = it.filterIndexed { i, char ->
                                char.isDigit() || ((char == '-') && (i == 0))
                            }
                            counterIntValue = counterValue.toIntOrNull()
                            checkIfCounterValueIsValid()
                        },
                        label = { Text(text = stringResource(R.string.current_value)) },
                        placeholder = { Text(text = stringResource(R.string.zero_by_default)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                        singleLine = true
                    )
                ExposedDropdownMenuBox(
                    expanded = labelDropdownExpanded,
                    onExpandedChange = { labelDropdownExpanded = !labelDropdownExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = if (counterLabel != null) counterLabel!!.name else stringResource(R.string.label_none),
                        onValueChange = {},
                        label = { Text(text = stringResource(R.string.label)) },
                        singleLine = true,
                        leadingIcon = {
                            if (counterLabel == null)
                                Box(modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .border(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        width = 2.dp,
                                        shape = CircleShape
                                    ))
                            else
                                Box(modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(counterLabel!!.color)))
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = labelDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = labelDropdownExpanded,
                        onDismissRequest = { labelDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.label_none)) },
                            onClick = {
                                counterLabel = null
                                labelDropdownExpanded = false
                            },
                            leadingIcon = { Box(modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .border(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    width = 2.dp,
                                    shape = CircleShape
                                )) },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                        labels.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(text = label.name) },
                                onClick = {
                                    counterLabel = label
                                    labelDropdownExpanded = false
                                },
                                leadingIcon = { Box(modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(label.color))) },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                ListItem(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .toggleable(
                            value = allowNegativeValues,
                            onValueChange = {
                                allowNegativeValues =
                                    it; checkIfCounterValueIsValid(); checkIfCounterDefaultValueIsValid()
                            },
                            role = Role.Switch
                        ),
                    headlineContent = { Text(text = stringResource(R.string.allow_negative)) },
                    trailingContent = {
                        Switch(
                            checked = allowNegativeValues,
                            onCheckedChange = null
                        )
                    }
                )
            }
        }
    )
}