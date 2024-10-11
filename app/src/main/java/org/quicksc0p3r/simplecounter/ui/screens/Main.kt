@file:OptIn(ExperimentalMaterial3Api::class)

package org.quicksc0p3r.simplecounter.ui.screens

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.NewLabel
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.quicksc0p3r.simplecounter.MAX_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.MIN_COUNTER_VALUE
import org.quicksc0p3r.simplecounter.NavRoutes
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.CounterSaver
import org.quicksc0p3r.simplecounter.db.CountersViewModel
import org.quicksc0p3r.simplecounter.db.Label
import org.quicksc0p3r.simplecounter.db.LabelsViewModel
import org.quicksc0p3r.simplecounter.db.counterSaver
import org.quicksc0p3r.simplecounter.db.labelSaver
import org.quicksc0p3r.simplecounter.evaluateMathOperation
import org.quicksc0p3r.simplecounter.json.GenerateJson
import org.quicksc0p3r.simplecounter.json.ParseJson
import org.quicksc0p3r.simplecounter.ui.components.CounterCard
import org.quicksc0p3r.simplecounter.ui.components.SearchTopAppBar
import org.quicksc0p3r.simplecounter.ui.dialogs.CounterCreateEditDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.CounterCreateEditTabletDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.DeleteDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.LabelCreationDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.LabelCreationTabletDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.MathOperationDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.MathOperationTabletDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.SpinnerDialog
import org.quicksc0p3r.simplecounter.ui.theme.Typography

@Composable
fun MainComposable(
    countersViewModel: CountersViewModel,
    labelsViewModel: LabelsViewModel,
    navController: NavHostController,
    setFullscreenCounter: (Int) -> Unit
) {
    val context = LocalContext.current
    var counterForDialog: Counter? by rememberSaveable(stateSaver = counterSaver()) { mutableStateOf(null) }
    var counterCreationDialogOpen by rememberSaveable { mutableStateOf(false) }
    var counterEditDialogOpen by rememberSaveable { mutableStateOf(false) }
    var counterDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    var labelCreationDialogOpen by rememberSaveable { mutableStateOf(false) }
    var importingDialogOpen by remember { mutableStateOf(false) }
    var exportingDialogOpen by remember { mutableStateOf(false) }
    var mathOperationDialogOpen by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val counters by countersViewModel.allCounters.observeAsState(listOf())
    val labels by labelsViewModel.allLabels.observeAsState(listOf())
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val contentResolver = context.contentResolver
    val metrics = context.resources.displayMetrics
    val dpWidth = metrics.widthPixels / metrics.density
    val createDocumentLauncher = rememberLauncherForActivityResult(CreateDocument("application/json")) { uri ->
        uri?.let {
            exportingDialogOpen = true
            contentResolver.openOutputStream(it)?.use { ostream ->
                ostream.write(GenerateJson(counters, labels))
            }
            exportingDialogOpen = false
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.export_success),
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    val openDocumentLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        uri?.let {
            try {
                contentResolver.openInputStream(it)?.use { istream ->
                    val countersAndLabels = ParseJson(istream.readBytes())
                    var labelJsonIdToRealId: MutableMap<Int, Int> = mutableMapOf()
                    var countersImported = 0
                    var labelsImported = 0
                    countersAndLabels?.let { cl ->
                        CoroutineScope(Dispatchers.IO).launch {
                            importingDialogOpen = true
                            cl.labels.forEach { label ->
                                labelJsonIdToRealId[label.id] = labelsViewModel.insertLabelWithIdReturn(
                                    Label(
                                        id = 0,
                                        name = label.name,
                                        color = label.color
                                    )
                                )
                                ++labelsImported
                            }
                            cl.counters.forEach { counter ->
                                countersViewModel.insertCounter(
                                    Counter(
                                        id = 0,
                                        name = counter.name,
                                        value = counter.value,
                                        defaultValue = counter.defaultValue,
                                        allowNegativeValues = counter.allowNegativeValues,
                                        labelId = labelJsonIdToRealId[counter.labelId]
                                    )
                                )
                                ++countersImported
                            }
                        }.invokeOnCompletion {
                            importingDialogOpen = false
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = when ((countersImported > 0) to (labelsImported > 0)) {
                                        true to true -> context.getString(R.string.import_both, countersImported, labelsImported)
                                        true to false -> context.getString(R.string.import_counters_only, countersImported)
                                        false to true -> context.getString(R.string.import_labels_only, labelsImported)
                                        false to false -> context.getString(R.string.import_nothing)
                                        else -> ""
                                    },
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                }
            } catch(e: Exception) {
                System.err.println("JSON import exception: ${e.message}")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    var currentLabelFilter: Label? by rememberSaveable(stateSaver = labelSaver()) { mutableStateOf(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val countersFiltered = counters.filter {
        (if (currentLabelFilter != null)
            it.labelId == currentLabelFilter!!.id
        else true) && (if (searchQuery.isNotEmpty())
            it.name.contains(searchQuery, ignoreCase = true)
        else true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(250.dp + WindowInsets.navigationBars.asPaddingValues().calculateStartPadding(LocalLayoutDirection.current))
            ) {
                NavigationDrawerItem(
                    icon = { Icon(imageVector = Icons.Rounded.NewLabel, contentDescription = stringResource(
                        R.string.create_label
                    )) },
                    label = { Text(text = stringResource(R.string.create_label)) },
                    selected = false,
                    onClick = {labelCreationDialogOpen = true},
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .fillMaxWidth()
                )
                LazyColumn {
                    item {
                        NavigationDrawerItem(
                            icon = { Box(modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .border(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    width = 2.dp,
                                    shape = CircleShape
                                )) },
                            label = { Text(text = stringResource(R.string.label_no_filter)) },
                            selected = currentLabelFilter == null,
                            onClick = { currentLabelFilter = null; scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                    items(labels, key = { it.id }) {label ->
                        var deleteDialogOpen by rememberSaveable { mutableStateOf(false) }

                        Row(horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            NavigationDrawerItem(
                                icon = { Box(modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(label.color))) },
                                label = { Text(
                                    text = label.name,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    onTextLayout = null
                                ) },
                                selected = label == currentLabelFilter,
                                onClick = { currentLabelFilter = label; scope.launch { drawerState.close() } },
                                modifier = Modifier
                                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                                    .width(175.dp)
                            )
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    PlainTooltip {
                                        Text(stringResource(R.string.delete))
                                    }
                                },
                                state = rememberTooltipState()
                            ) {
                                IconButton(
                                    onClick = { deleteDialogOpen = true },
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = stringResource(
                                        R.string.delete
                                    ))
                                }
                            }
                        }

                        if (deleteDialogOpen) DeleteDialog(
                            dismiss = { deleteDialogOpen = false },
                            isLabel = true,
                            countersViewModel = countersViewModel,
                            labelsViewModel = labelsViewModel,
                            label = label,
                            clearLabelFilter = { if (currentLabelFilter == label) currentLabelFilter = null }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SearchTopAppBar(
                    scrollBehavior = scrollBehavior,
                    scope = scope,
                    drawerState = drawerState,
                    navController = navController,
                    changeSearchQuery = {query ->
                        searchQuery = query
                    },
                    clearSearchQuery = {searchQuery = ""},
                    exportData = {
                        createDocumentLauncher.launch("SimpleCounterData.json")
                    },
                    importData = {
                        openDocumentLauncher.launch(arrayOf("application/json"))
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                TooltipBox(
                    modifier = Modifier.padding(
                        end = WindowInsets.navigationBars.asPaddingValues().calculateEndPadding(LocalLayoutDirection.current)
                    ),
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(R.string.create_counter))
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    FloatingActionButton(
                        onClick = {counterCreationDialogOpen = true},
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        content = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = stringResource(R.string.create_counter)
                            )
                        }
                    )
                }
            }
        ) { padding ->
            if (counterCreationDialogOpen) {
                if (dpWidth < 600)
                    CounterCreateEditDialog(
                        dismiss = { counterCreationDialogOpen = false },
                        countersViewModel = countersViewModel,
                        labelsViewModel = labelsViewModel,
                        isEdit = false
                    )
                else
                    CounterCreateEditTabletDialog(
                        dismiss = { counterCreationDialogOpen = false },
                        countersViewModel = countersViewModel,
                        labelsViewModel = labelsViewModel,
                        isEdit = false
                    )
            }
            if (counterEditDialogOpen) {
                if (dpWidth < 600)
                    CounterCreateEditDialog(
                        dismiss = { counterEditDialogOpen = false },
                        countersViewModel = countersViewModel,
                        labelsViewModel = labelsViewModel,
                        isEdit = true,
                        counter = counterForDialog
                    )
                else
                    CounterCreateEditTabletDialog(
                        dismiss = { counterEditDialogOpen = false },
                        countersViewModel = countersViewModel,
                        labelsViewModel = labelsViewModel,
                        isEdit = true,
                        counter = counterForDialog
                    )
            }
            if (counterDeleteDialogOpen) DeleteDialog(
                dismiss = { counterDeleteDialogOpen = false },
                isLabel = false,
                countersViewModel = countersViewModel,
                counter = counterForDialog
            )
            if (labelCreationDialogOpen) {
                if (dpWidth < 600)
                    LabelCreationDialog(
                        dismiss = { labelCreationDialogOpen = false },
                        viewModel = labelsViewModel
                    )
                else
                    LabelCreationTabletDialog(
                        dismiss = { labelCreationDialogOpen = false },
                        viewModel = labelsViewModel
                    )
            }
            if (importingDialogOpen) SpinnerDialog(stringResource(R.string.importing))
            if (exportingDialogOpen) SpinnerDialog(stringResource(R.string.exporting))
            if (mathOperationDialogOpen)
                if (dpWidth < 600)
                    MathOperationDialog(
                        dismiss = { mathOperationDialogOpen = false },
                        counterValue = counterForDialog!!.value,
                        minValue = if (counterForDialog!!.allowNegativeValues) MIN_COUNTER_VALUE else 0,
                        maxValue = MAX_COUNTER_VALUE
                    ) { value ->
                        countersViewModel.updateCounter2(Counter(
                            counterForDialog!!.id,
                            counterForDialog!!.name,
                            value,
                            counterForDialog!!.defaultValue,
                            counterForDialog!!.labelId,
                            counterForDialog!!.allowNegativeValues
                        ))
                    }
                else
                    MathOperationTabletDialog(
                        dismiss = { mathOperationDialogOpen = false },
                        counterValue = counterForDialog!!.value,
                        minValue = if (counterForDialog!!.allowNegativeValues) MIN_COUNTER_VALUE else 0,
                        maxValue = MAX_COUNTER_VALUE
                    ) { value ->
                        countersViewModel.updateCounter2(Counter(
                            counterForDialog!!.id,
                            counterForDialog!!.name,
                            value,
                            counterForDialog!!.defaultValue,
                            counterForDialog!!.labelId,
                            counterForDialog!!.allowNegativeValues
                        ))
                    }

            if (countersFiltered.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty())
                            stringResource(R.string.no_counters_found)
                        else if (currentLabelFilter != null)
                            stringResource(R.string.no_counters_label)
                        else
                            stringResource(R.string.no_counters),
                        style = Typography.bodyMedium,
                        color = Color.LightGray,
                        onTextLayout = null
                    )
                }
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.padding(padding),
                    columns = GridCells.Adaptive(minSize = 300.dp)
                ) {
                    items(countersFiltered, key = { it.id }) { counter ->
                        CounterCard(
                            counter = counter,
                            snackbarHostState = snackbarHostState,
                            updateCounter = {value ->
                                countersViewModel.updateCounter2(Counter(
                                    counter.id,
                                    counter.name,
                                    value,
                                    counter.defaultValue,
                                    counter.labelId,
                                    counter.allowNegativeValues
                                ))
                            },
                            setFullscreenCounter = {
                                setFullscreenCounter(counter.id)
                                navController.navigate(NavRoutes.FullscreenView.route)
                            },
                            openEditDialog = {
                                counterForDialog = it
                                counterEditDialogOpen = true
                            },
                            openDeleteDialog = {
                                counterForDialog = it
                                counterDeleteDialogOpen = true
                            },
                            openMathOperationDialog = {
                                counterForDialog = it
                                mathOperationDialogOpen = true
                            },
                            getLabelFlowById = {id ->
                                labelsViewModel.getLabelById(id)
                            }
                        )
                    }
                }
            }
        }
    }
}