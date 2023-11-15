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
    val lightDarkToken by manager.lightDarkSettingFlow.collectAsState(initial = LightDarkSetting.SYSTEM.value)
    val colorSettingToken by manager.colorSettingFlow.collectAsState(initial = "")
    val isDarkTheme = ((lightDarkToken == LightDarkSetting.SYSTEM.value) && isSystemInDarkTheme()) || (lightDarkToken == LightDarkSetting.DARK.value)
    val systemUIController = rememberSystemUiController()
    val colorScheme = if (isDarkTheme)
        when (colorSettingToken) {
            ColorSetting.SYSTEM.value -> dynamicDarkColorScheme(context)
            ColorSetting.RED.value -> RedDarkColorScheme
            ColorSetting.ORANGE.value -> OrangeDarkColorScheme
            ColorSetting.YELLOW.value -> YellowDarkColorScheme
            ColorSetting.GREEN.value -> GreenDarkColorScheme
            ColorSetting.BLUE.value -> BlueDarkColorScheme
            ColorSetting.PURPLE.value -> PurpleDarkColorScheme
            else -> PurpleDarkColorScheme
        }
    else
        when (colorSettingToken) {
            ColorSetting.SYSTEM.value -> dynamicLightColorScheme(context)
            ColorSetting.RED.value -> RedLightColorScheme
            ColorSetting.ORANGE.value -> OrangeLightColorScheme
            ColorSetting.YELLOW.value -> YellowLightColorScheme
            ColorSetting.GREEN.value -> GreenLightColorScheme
            ColorSetting.BLUE.value -> BlueLightColorScheme
            ColorSetting.PURPLE.value -> PurpleLightColorScheme
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