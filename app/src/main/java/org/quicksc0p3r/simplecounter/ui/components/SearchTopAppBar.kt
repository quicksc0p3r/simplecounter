package org.quicksc0p3r.simplecounter.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.quicksc0p3r.simplecounter.NavRoutes
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController,
    changeSearchQuery: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    exportData: () -> Unit,
    importData: () -> Unit
) {
    var topBarMenuExpanded by remember { mutableStateOf(false) }
    var topBarIsInSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Crossfade(targetState = topBarIsInSearchMode, label = "") {topBarSearchActive ->
        if (!topBarSearchActive) {
            TopAppBar(
                title = {
                    Text(text = "Simple Counter")
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.label_drawer))
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Rounded.Menu, contentDescription = stringResource(
                                R.string.label_drawer
                            )
                            )
                        }
                    }
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.search))
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = { topBarIsInSearchMode = true }) {
                            Icon(imageVector = Icons.Rounded.Search, contentDescription = stringResource(
                                R.string.search
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
                        IconButton(content = {
                            Icon(imageVector = Icons.Rounded.MoreVert,
                                contentDescription = stringResource(R.string.more_options)
                            )
                        }, onClick = { topBarMenuExpanded = !topBarMenuExpanded })
                    }

                    DropdownMenu(
                        expanded = topBarMenuExpanded,
                        content = {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.settings)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Sharp.Settings,
                                        contentDescription = stringResource(R.string.settings)
                                    )
                                },
                                onClick = {topBarMenuExpanded = false; navController.navigate(
                                    NavRoutes.Settings.route) }
                            )
                            /* TODO
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.export_data)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.FileUpload,
                                        contentDescription = stringResource(R.string.export_data)
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    exportData()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.import_data)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.FileDownload,
                                        contentDescription = stringResource(R.string.import_data)
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    importData()
                                }
                            )*/
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.about)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = stringResource(R.string.about)
                                    )
                                },
                                onClick = { topBarMenuExpanded = false; navController.navigate(
                                    NavRoutes.About.route) }
                            )
                        },
                        onDismissRequest = { topBarMenuExpanded = false }
                    )
                }
            )
        }
        else {
            TopAppBar(
                title = {
                    val focusManager = LocalFocusManager.current
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it; changeSearchQuery(it) },
                        placeholder = { Text(text = stringResource(R.string.search), style = Typography.bodyMedium) },
                        leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = stringResource(
                            R.string.search
                        )
                        ) },
                        shape = CircleShape,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        textStyle = Typography.bodyMedium,
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier
                            .height(50.dp)
                            .padding(end = 10.dp)
                            .fillMaxWidth(),
                        singleLine = true
                    )
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
                        IconButton(onClick = { topBarIsInSearchMode = false; clearSearchQuery(); searchQuery = "" }) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(
                                R.string.back
                            )
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    }
}