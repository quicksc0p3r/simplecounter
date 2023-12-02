package org.quicksc0p3r.simplecounter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.quicksc0p3r.simplecounter.MAX_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.MIN_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.NavRoutes
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.CountersViewModel
import org.quicksc0p3r.simplecounter.db.LabelsViewModel
import org.quicksc0p3r.simplecounter.settings.SettingsManager
import org.quicksc0p3r.simplecounter.ui.dialogs.CounterCreateEditDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.DeleteDialog
import org.quicksc0p3r.simplecounter.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenView(
    counterId: Int,
    countersViewModel: CountersViewModel,
    labelsViewModel: LabelsViewModel,
    navController: NavHostController
) {
    var counterIsDeleted by remember { mutableStateOf(false) }
    if (!counterIsDeleted) {
        val counter by countersViewModel.getCounterById(counterId).collectAsState(initial = Counter(-1))
        val label = if (counter.labelId != null) labelsViewModel.getLabelById(counter.labelId!!).collectAsState(initial = null) else null
        var allowAdd by rememberSaveable { mutableStateOf(counter.value < MAX_COUNTER_VALUE) }
        var allowSubtract by rememberSaveable { mutableStateOf((counter.allowNegativeValues && (counter.value > MIN_COUNTER_VALUE)) || (counter.value > 0)) }
        val context = LocalContext.current
        val hapticFeedback = LocalHapticFeedback.current
        val metrics = context.resources.displayMetrics
        val dpWidth = metrics.widthPixels / metrics.density
        val referenceSize = (dpWidth - 30) / 2
        var editDialogOpen by remember { mutableStateOf(false) }
        var deleteDialogOpen by remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        val snackbarHostState = remember { SnackbarHostState() }
        val manager = SettingsManager(context)
        val volumeKeysTipWasShownFlow = manager.volumeKeysSnackbarTipWasShownFlow.collectAsState(initial = false)
        val hapticFeedbackOnTouchToken = manager.hapticFeedbackOnTouchFlow.collectAsState(initial = false)
        val hapticFeedbackOnVolumeToken = manager.hapticFeedbackOnVolumeFlow.collectAsState(initial = false)

        fun checkIfAdditionIsAllowed() {
            allowAdd = counter.value < MAX_COUNTER_VALUE
        }

        fun checkIfSubtractionIsAllowed() {
            allowSubtract = (counter.allowNegativeValues && (counter.value > MIN_COUNTER_VALUE)) || (counter.value > 0)
        }

        fun subtractFromCounter() {
            countersViewModel.updateCounter2(
                Counter(
                counter.id,
                counter.name,
                counter.value - 1,
                counter.defaultValue,
                counter.labelId,
                counter.allowNegativeValues)
            )
        }
        fun addToCounter() {
            countersViewModel.updateCounter2(
                Counter(
                counter.id,
                counter.name,
                counter.value + 1,
                counter.defaultValue,
                counter.labelId,
                counter.allowNegativeValues)
            )
        }
        fun resetCounter() {
            countersViewModel.updateCounter2(Counter(counter.id, counter.name, counter.defaultValue, counter.defaultValue, counter.labelId, counter.allowNegativeValues))
        }

        Scaffold(
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent {
                    when (it.key) {
                        Key.VolumeUp -> {
                            if (allowAdd && it.type == KeyEventType.KeyDown) {
                                addToCounter()
                                if (hapticFeedbackOnVolumeToken.value)
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            true
                        }
                        Key.VolumeDown -> {
                            if (allowSubtract && it.type == KeyEventType.KeyDown) {
                                subtractFromCounter()
                                if (hapticFeedbackOnVolumeToken.value)
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            true
                        }
                        else -> false
                    }
                },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = counter.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                        }
                    },
                    navigationIcon = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.back))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack(NavRoutes.Main.route, false)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                    },
                    actions = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.edit))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(onClick = { editDialogOpen = true }) {
                                Icon(imageVector = Icons.Rounded.Edit, contentDescription = stringResource(
                                    R.string.edit
                                ))
                            }
                        }
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.reset))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(onClick = { resetCounter() }) {
                                Icon(imageVector = Icons.Rounded.RestartAlt, contentDescription = stringResource(
                                    R.string.reset
                                ))
                            }
                        }
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.delete))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(onClick = { deleteDialogOpen = true }) {
                                Icon(imageVector = Icons.Rounded.Delete, contentDescription = stringResource(
                                    R.string.delete
                                ))
                            }
                        }
                    }
                )
            },
            bottomBar = {
                Row(modifier = Modifier
                    .padding(bottom = 2.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.subtract))
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .width(referenceSize.dp)
                                .height(
                                    if (metrics.widthPixels > metrics.heightPixels)
                                        (referenceSize / 3).dp
                                    else referenceSize.dp
                                ),
                            onClick = {
                                countersViewModel.updateCounter2(
                                    Counter(
                                        counter.id,
                                        counter.name,
                                        counter.value - 1,
                                        counter.defaultValue,
                                        counter.labelId,
                                        counter.allowNegativeValues
                                    )
                                )
                                if (hapticFeedbackOnTouchToken.value)
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            enabled = allowSubtract,
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    modifier = Modifier.size((referenceSize / if (metrics.widthPixels > metrics.heightPixels) 6 else 3).dp),
                                    imageVector = Icons.Rounded.Remove,
                                    contentDescription = stringResource(R.string.subtract)
                                )
                            }
                        }
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.add))
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .width(referenceSize.dp)
                                .height(
                                    if (metrics.widthPixels > metrics.heightPixels)
                                        (referenceSize / 3).dp
                                    else referenceSize.dp
                                ),
                            onClick = {
                                countersViewModel.updateCounter2(
                                    Counter(
                                        counter.id,
                                        counter.name,
                                        counter.value + 1,
                                        counter.defaultValue,
                                        counter.labelId,
                                        counter.allowNegativeValues
                                    )
                                )
                                if (hapticFeedbackOnTouchToken.value)
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            enabled = allowAdd,
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    modifier = Modifier.size((referenceSize / if (metrics.widthPixels > metrics.heightPixels) 6 else 3).dp),
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = stringResource(R.string.add)
                                )
                            }
                        }
                    }
                    checkIfAdditionIsAllowed()
                    checkIfSubtractionIsAllowed()
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            focusRequester.requestFocus()
            if (!volumeKeysTipWasShownFlow.value) {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = getString(context, R.string.volume_keys_tip),
                        duration = SnackbarDuration.Long,
                        actionLabel = getString(context, R.string.got_it)
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        manager.storeVolumeKeysSnackbarTipWasShown(true)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                @Composable
                fun dpToSp(dp: Dp) = with(LocalDensity.current) {
                    dp.toSp()
                }
                val counterValue = counter.value.toString()
                val textStyle = Typography(
                    displayLarge = TextStyle(
                        fontSize =
                        if (metrics.widthPixels < metrics.heightPixels)
                            (if (counterValue.length <= 4) dpToSp((referenceSize * 108 / 165).dp)
                            else if (counterValue.length <= 6) dpToSp((referenceSize * 88 / 165).dp)
                            else if (counterValue.length <= 8) dpToSp((referenceSize * 80 / 165).dp)
                            else dpToSp((referenceSize * 60 / 165).dp))
                        else
                            dpToSp(((if (counterValue.length <= 4) referenceSize * 108 / 165
                            else if (counterValue.length <= 6) referenceSize * 88 / 165
                            else if (counterValue.length <= 8) referenceSize * 80 / 165
                            else referenceSize * 60 / 165) / 2).dp)
                    )
                )
                Text(
                    modifier = Modifier.pointerInput(counter.defaultValue) {
                        detectTapGestures(
                            onLongPress = {
                                resetCounter()
                            }
                        )
                    },
                    text = counterValue,
                    style = textStyle.displayLarge
                )
            }
            if (editDialogOpen) CounterCreateEditDialog(
                dismiss = { editDialogOpen = false },
                countersViewModel = countersViewModel,
                labelsViewModel = labelsViewModel,
                isEdit = true,
                counter = counter
            )
            if (deleteDialogOpen) DeleteDialog(
                dismiss = { deleteDialogOpen = false },
                counter = counter,
                isLabel = false,
                countersViewModel = countersViewModel,
                beforeDelete = {
                    counterIsDeleted = true
                    navController.popBackStack(NavRoutes.Main.route, false)
                }
            )
        }
    }
}