package org.quicksc0p3r.simplecounter.settings

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.quicksc0p3r.simplecounter.R

@Composable
fun lightDarkSettingListFactory(): List<Setting> {
    return listOf(
        Setting(
            name = stringResource(R.string.system_default),
            value = LightDarkSetting.SYSTEM.value,
            minSDK = Build.VERSION_CODES.Q,
            minVer = "10"
        ),
        Setting(
            name = stringResource(R.string.light_theme),
            value = LightDarkSetting.LIGHT.value
        ),
        Setting(
            name = stringResource(R.string.dark_theme),
            value = LightDarkSetting.DARK.value
        )
    )
}

@Composable
fun colorSchemeSettingListFactory(): List<Setting> {
    return listOf(
        Setting(
            name = stringResource(R.string.system_default),
            value = ColorSetting.SYSTEM.value,
            minSDK = Build.VERSION_CODES.S,
            minVer = "12"
        ),
        Setting(
            name = stringResource(R.string.red_scheme),
            value = ColorSetting.RED.value
        ),
        Setting(
            name = stringResource(R.string.orange_scheme),
            value = ColorSetting.ORANGE.value
        ),
        Setting(
            name = stringResource(R.string.yellow_scheme),
            value = ColorSetting.YELLOW.value
        ),
        Setting(
            name = stringResource(R.string.green_scheme),
            value = ColorSetting.GREEN.value
        ),
        Setting(
            name = stringResource(R.string.blue_scheme),
            value = ColorSetting.BLUE.value
        ),
        Setting(
            name = stringResource(R.string.purple_scheme),
            value = ColorSetting.PURPLE.value
        )
    )
}