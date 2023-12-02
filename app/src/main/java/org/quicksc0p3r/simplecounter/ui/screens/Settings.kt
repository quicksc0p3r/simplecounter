@file:OptIn(ExperimentalMaterial3Api::class)

package org.quicksc0p3r.simplecounter.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.quicksc0p3r.simplecounter.NavRoutes
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.settings.ColorSetting
import org.quicksc0p3r.simplecounter.settings.CounterCardStyleSetting
import org.quicksc0p3r.simplecounter.settings.LightDarkSetting
import org.quicksc0p3r.simplecounter.settings.SettingsManager
import org.quicksc0p3r.simplecounter.settings.colorSchemeSettingListFactory
import org.quicksc0p3r.simplecounter.settings.lightDarkSettingListFactory
import org.quicksc0p3r.simplecounter.ui.dialogs.HapticFeedbackSettingDialog
import org.quicksc0p3r.simplecounter.ui.dialogs.SettingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavHostController) {
    val context = LocalContext.current
    val manager = SettingsManager(context)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lightDarkToken = manager.lightDarkSettingFlow.collectAsState(initial = LightDarkSetting.LIGHT.ordinal)
    val colorSettingToken = manager.colorSettingFlow.collectAsState(initial = ColorSetting.PURPLE.ordinal)
    val counterCardStyleToken = manager.counterCardStyleSettingFlow.collectAsState(initial = CounterCardStyleSetting.NORMAL.ordinal)
    val hapticFeedbackOnTouchToken = manager.hapticFeedbackOnTouchFlow.collectAsState(initial = false)
    val hapticFeedbackOnVolumeToken = manager.hapticFeedbackOnVolumeFlow.collectAsState(initial = false)
    var lightDarkDialogOpen by remember { mutableStateOf(false) }
    var colorSettingDialogOpen by remember { mutableStateOf(false) }
    var hapticFeedbackDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(
                title = {Text(stringResource(R.string.settings))},
                scrollBehavior = scrollBehavior,
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
                            },
                            content = { Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                            }
                        )
                    }
                }
            )
        },
        content = {padding ->
            if (lightDarkDialogOpen)
                SettingDialog(
                    dismiss = { lightDarkDialogOpen = false },
                    settingName = stringResource(R.string.light_dark_theme),
                    settings = lightDarkSettingListFactory(),
                    currentValue = lightDarkToken.value,
                    changeSetting = {setting ->
                        CoroutineScope(Dispatchers.IO).launch {
                            manager.storeLightDarkSetting(setting)
                        }
                    }
                )
            if (colorSettingDialogOpen)
                SettingDialog(
                    dismiss = { colorSettingDialogOpen = false },
                    settingName = stringResource(R.string.color_scheme),
                    settings = colorSchemeSettingListFactory(),
                    currentValue = colorSettingToken.value,
                    changeSetting = {setting ->
                        CoroutineScope(Dispatchers.IO).launch {
                            manager.storeColorSetting(setting)
                        }
                    }
                )
            if (hapticFeedbackDialogOpen)
                HapticFeedbackSettingDialog(
                    dismiss = { hapticFeedbackDialogOpen = false },
                    manager = manager,
                    currentTouchSetting = hapticFeedbackOnTouchToken.value,
                    currentVolumeSetting = hapticFeedbackOnVolumeToken.value
                )

            LazyColumn(modifier = Modifier.padding(padding)) {
                item {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.light_dark_theme)) },
                        supportingContent = { Text(
                            when (lightDarkToken.value) {
                                LightDarkSetting.SYSTEM.ordinal -> stringResource(R.string.system_default)
                                LightDarkSetting.LIGHT.ordinal -> stringResource(R.string.light_theme)
                                LightDarkSetting.DARK.ordinal -> stringResource(R.string.dark_theme)
                                else -> ""
                            }
                        )},
                        modifier = Modifier.clickable { lightDarkDialogOpen = true }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.color_scheme)) },
                        supportingContent = { Text(
                            when (colorSettingToken.value) {
                                ColorSetting.SYSTEM.ordinal -> stringResource(R.string.system_default)
                                ColorSetting.RED.ordinal -> stringResource(R.string.red_scheme)
                                ColorSetting.ORANGE.ordinal -> stringResource(R.string.orange_scheme)
                                ColorSetting.YELLOW.ordinal -> stringResource(R.string.yellow_scheme)
                                ColorSetting.GREEN.ordinal -> stringResource(R.string.green_scheme)
                                ColorSetting.BLUE.ordinal -> stringResource(R.string.blue_scheme)
                                ColorSetting.PURPLE.ordinal -> stringResource(R.string.purple_scheme)
                                else -> ""
                            }
                        )},
                        modifier = Modifier.clickable { colorSettingDialogOpen = true }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.haptic_feedback)) },
                        supportingContent = { Text(
                            when (hapticFeedbackOnTouchToken.value to hapticFeedbackOnVolumeToken.value) {
                                false to false -> stringResource(R.string.off)
                                false to true -> stringResource(R.string.haptic_feedback_only_volume)
                                true to false -> stringResource(R.string.haptic_feedback_only_touch)
                                true to true -> stringResource(R.string.always)
                                else -> ""
                            }
                        )},
                        modifier = Modifier.clickable { hapticFeedbackDialogOpen = true }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.compact_counter_cards)) },
                        trailingContent = {
                            Checkbox(
                                checked = counterCardStyleToken.value == CounterCardStyleSetting.COMPACT.ordinal,
                                onCheckedChange = null
                            )
                        },
                        modifier = Modifier.toggleable(
                            value = counterCardStyleToken.value == CounterCardStyleSetting.COMPACT.ordinal,
                            onValueChange = {
                                if (counterCardStyleToken.value == CounterCardStyleSetting.COMPACT.ordinal)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        manager.storeCounterCardStyleSetting(CounterCardStyleSetting.NORMAL.ordinal)
                                    }
                                else
                                    CoroutineScope(Dispatchers.IO).launch {
                                        manager.storeCounterCardStyleSetting(CounterCardStyleSetting.COMPACT.ordinal)
                                    }
                            },
                            role = Role.Checkbox
                        )
                    )
                }
            }
        }
    )
}