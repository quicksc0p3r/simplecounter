package org.quicksc0p3r.simplecounter.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.util.TypedValueCompat.pxToDp
import androidx.core.util.TypedValueCompat.pxToSp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.quicksc0p3r.simplecounter.MAX_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.MIN_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.Label
import org.quicksc0p3r.simplecounter.settings.CounterCardStyleSetting
import org.quicksc0p3r.simplecounter.settings.SettingsManager
import org.quicksc0p3r.simplecounter.ui.theme.Typography

class CounterPreviewParameterProvider: PreviewParameterProvider<Counter> {
    override val values = sequenceOf(
        Counter(id = 0, name = "Testing", value = 42)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterCard(
    @PreviewParameter(CounterPreviewParameterProvider::class) counter: Counter,
    snackbarHostState: SnackbarHostState? = null,
    setFullscreenCounter: () -> Unit = {},
    updateCounter: (Int) -> Unit = {},
    openEditDialog: (Counter) -> Unit = {},
    openDeleteDialog: (Counter) -> Unit = {},
    getLabelFlowById: (Int) -> Flow<Label> = {flowOf()}
) {
    val context = LocalContext.current
    val metrics = context.resources.displayMetrics
    var allowAdd by rememberSaveable { mutableStateOf(counter.value < MAX_COUNTER_VALUE) }
    var allowSubtract by rememberSaveable { mutableStateOf((counter.allowNegativeValues && (counter.value > MIN_COUNTER_VALUE)) || (counter.value > 0)) }
    var counterMenuExpanded by remember { mutableStateOf(false) }
    val manager = SettingsManager(context)
    val counterCardStyleToken = manager.counterCardStyleSettingFlow.collectAsState(initial = "")
    val resetSnackbarTipWasShownToken = manager.resetSnackbarTipWasShownFlow.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val label = if (counter.labelId != null) getLabelFlowById(counter.labelId).collectAsState(initial = null) else null
    var counterCardWidth by remember { mutableIntStateOf(0) }

    fun checkIfAdditionIsAllowed() {
        allowAdd = counter.value < MAX_COUNTER_VALUE
    }

    fun checkIfSubtractionIsAllowed() {
        allowSubtract = (counter.allowNegativeValues && (counter.value > MIN_COUNTER_VALUE)) || (counter.value > 0)
    }

    AnimatedVisibility(visible = true) {
        ElevatedCard(
            modifier = Modifier
                .padding(bottom = 6.dp, top = 6.dp, start = 8.dp, end = 8.dp)
                .height(if (counterCardStyleToken.value == CounterCardStyleSetting.COMPACT.ordinal) 125.dp else 200.dp)
                .onGloballyPositioned { coordinates ->
                    counterCardWidth = coordinates.size.width
                },
            onClick = {
                setFullscreenCounter()
            }
        ) {
            Column{
                Row(modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){
                    Column{
                        Text(
                            text = counter.name,
                            style = Typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(
                                (counterCardWidth / context.resources.displayMetrics.density - 55).dp
                            )
                        )
                        if (label != null)
                            if (label.value != null)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Box(modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(label.value!!.color)))
                                    Text(
                                        text = label.value!!.name,
                                        style = Typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.width(
                                            (counterCardWidth / context.resources.displayMetrics.density - 65).dp
                                        )
                                    )
                                }
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.more_options))
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(
                            onClick = { counterMenuExpanded = !counterMenuExpanded }
                        ){
                            Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = stringResource(
                                R.string.more_options
                            )
                            )
                            DropdownMenu(
                                expanded = counterMenuExpanded,
                                onDismissRequest = { counterMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.edit)) },
                                    onClick = { counterMenuExpanded = false; openEditDialog(counter) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Edit, contentDescription = stringResource(
                                                R.string.edit
                                            )
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.reset)) },
                                    onClick = {
                                        updateCounter(counter.defaultValue)
                                        counterMenuExpanded = false
                                        if (!resetSnackbarTipWasShownToken.value) {
                                            scope.launch {
                                                snackbarHostState?.showSnackbar(
                                                    message = ContextCompat.getString(context,
                                                        R.string.reset_snackbar_tip
                                                    ),
                                                    duration = SnackbarDuration.Long,
                                                    actionLabel = ContextCompat.getString(context,
                                                        R.string.got_it
                                                    )
                                                )
                                            }
                                            CoroutineScope(Dispatchers.IO).launch {
                                                manager.storeResetSnackbarTipWasShown(true)
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.RestartAlt, contentDescription = stringResource(
                                                R.string.reset
                                            )
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.delete)) },
                                    onClick = {
                                        counterMenuExpanded = false
                                        openDeleteDialog(counter)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete, contentDescription = stringResource(
                                                R.string.delete
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    fun calculatePadding(): Dp {
                        // Can probably be improved
                        var result = 56 - (56 - pxToSp(dpToPx(56F, metrics), metrics))
                        if (pxToDp(counterCardWidth.toFloat(), metrics) < 330)
                            result -= 14
                        return result.dp
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = calculatePadding())
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        val counterValue = counter.value.toString()
                        val textStyle = Typography(
                            displayMedium = TextStyle(
                                fontSize =
                                if (counterValue.length <= 4) 45.sp
                                else if (counterValue.length <= 6) 38.sp
                                else if (counterValue.length <= 8) 30.sp
                                else 25.sp
                            )
                        )

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.subtract))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            FilledTonalIconButton(
                                enabled = allowSubtract,
                                onClick = {
                                    updateCounter(counter.value - 1)
                                }
                            ){
                                Icon(imageVector = Icons.Rounded.Remove, contentDescription = stringResource(
                                    R.string.subtract
                                )
                                )
                            }
                        }
                        Text(
                            modifier = Modifier
                                .pointerInput(counter.defaultValue) {
                                detectTapGestures(
                                    onLongPress = {
                                        updateCounter(counter.defaultValue)
                                        CoroutineScope(Dispatchers.IO).launch {
                                            manager.storeResetSnackbarTipWasShown(true)
                                        }
                                    }
                                )
                            },
                            text = counterValue,
                            style = textStyle.displayMedium
                        )
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.add))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            FilledTonalIconButton(
                                enabled = allowAdd,
                                onClick = {
                                    updateCounter(counter.value + 1)
                                }
                            ){
                                Icon(imageVector = Icons.Rounded.Add, contentDescription = stringResource(
                                    R.string.add
                                )
                                )
                            }
                        }
                        checkIfAdditionIsAllowed()
                        checkIfSubtractionIsAllowed()
                    }
                }
            }
        }
    }
}