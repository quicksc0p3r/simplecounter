@file:OptIn(ExperimentalMaterial3Api::class)

package org.quicksc0p3r.simplecounter.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.quicksc0p3r.simplecounter.NavRoutes
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.db.Counter
import org.quicksc0p3r.simplecounter.db.CountersViewModel
import org.quicksc0p3r.simplecounter.db.Label
import org.quicksc0p3r.simplecounter.db.LabelsViewModel
import org.quicksc0p3r.simplecounter.ui.components.CounterCard
import org.quicksc0p3r.simplecounter.ui.components.SearchTopAppBar
import org.quicksc0p3r.simplecounter.ui.dialogs.CounterCreateEditDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.DeleteDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.LabelCreationDialog
import org.quicksc0p3r.simplecounter.ui.theme.Typography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainComposable(
    countersViewModel: CountersViewModel,
    labelsViewModel: LabelsViewModel,
    navController: NavHostController,
    setFullscreenCounter: (Int) -> Unit
) {
    val context = LocalContext.current
    var counterForDialog: Counter? by remember { mutableStateOf(null) }
    var counterCreationDialogOpen by remember { mutableStateOf(false) }
    var counterEditDialogOpen by remember { mutableStateOf(false) }
    var counterDeleteDialogOpen by remember { mutableStateOf(false) }
    var labelCreationDialogOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val counters by countersViewModel.allCounters.observeAsState(listOf())
    val labels by labelsViewModel.allLabels.observeAsState(listOf())
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var import by remember { mutableStateOf(false) }
    val nullableLabel: Label? = null
    var currentLabelFilter by remember { mutableStateOf(nullableLabel) }
    var searchQuery by remember { mutableStateOf("") }
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
            ModalDrawerSheet(modifier = Modifier.width(250.dp)) {
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
                        var deleteDialogOpen by remember { mutableStateOf(false) }

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
                                    .animateItemPlacement()
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
                    exportData = {},
                    importData = {}
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                TooltipBox(
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
            if (counterCreationDialogOpen) CounterCreateEditDialog({
                counterCreationDialogOpen = false
            }, countersViewModel, labelsViewModel, false)
            if (counterEditDialogOpen) CounterCreateEditDialog(
                dismiss = { counterEditDialogOpen = false },
                countersViewModel = countersViewModel,
                labelsViewModel = labelsViewModel,
                isEdit = true,
                counter = counterForDialog
            )
            if (counterDeleteDialogOpen) DeleteDialog(
                dismiss = { counterDeleteDialogOpen = false },
                isLabel = false,
                countersViewModel = countersViewModel,
                counter = counterForDialog
            )
            if (labelCreationDialogOpen) LabelCreationDialog(dismiss = {
                labelCreationDialogOpen = false
            }, labelsViewModel)

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
                            modifier = Modifier.animateItemPlacement(),
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