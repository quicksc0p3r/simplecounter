package org.quicksc0p3r.simplecounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.quicksc0p3r.simplecounter.settings.ColorSetting
import org.quicksc0p3r.simplecounter.settings.LightDarkSetting
import org.quicksc0p3r.simplecounter.settings.SettingsManager

@Composable
fun SimpleCounterTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val manager = SettingsManager(context)
    val lightDarkToken by manager.lightDarkSettingFlow.collectAsState(initial = LightDarkSetting.SYSTEM.ordinal)
    val colorSettingToken by manager.colorSettingFlow.collectAsState(initial = "")
    val isDarkTheme = ((lightDarkToken == LightDarkSetting.SYSTEM.ordinal) && isSystemInDarkTheme()) || (lightDarkToken == LightDarkSetting.DARK.ordinal)
    val systemUIController = rememberSystemUiController()
    val colorScheme = if (isDarkTheme)
        when (colorSettingToken) {
            ColorSetting.SYSTEM.ordinal -> dynamicDarkColorScheme(context)
            ColorSetting.RED.ordinal -> RedDarkColorScheme
            ColorSetting.ORANGE.ordinal -> OrangeDarkColorScheme
            ColorSetting.YELLOW.ordinal -> YellowDarkColorScheme
            ColorSetting.GREEN.ordinal -> GreenDarkColorScheme
            ColorSetting.BLUE.ordinal -> BlueDarkColorScheme
            ColorSetting.PURPLE.ordinal -> PurpleDarkColorScheme
            else -> PurpleDarkColorScheme
        }
    else
        when (colorSettingToken) {
            ColorSetting.SYSTEM.ordinal -> dynamicLightColorScheme(context)
            ColorSetting.RED.ordinal -> RedLightColorScheme
            ColorSetting.ORANGE.ordinal -> OrangeLightColorScheme
            ColorSetting.YELLOW.ordinal -> YellowLightColorScheme
            ColorSetting.GREEN.ordinal -> GreenLightColorScheme
            ColorSetting.BLUE.ordinal -> BlueLightColorScheme
            ColorSetting.PURPLE.ordinal -> PurpleLightColorScheme
            else -> PurpleLightColorScheme
        }
    SideEffect {
        systemUIController.setSystemBarsColor(color = colorScheme.background)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}