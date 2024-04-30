package org.quicksc0p3r.simplecounter.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.quicksc0p3r.simplecounter.BuildConfig
import org.quicksc0p3r.simplecounter.settings.ColorSetting
import org.quicksc0p3r.simplecounter.settings.LightDarkSetting
import org.quicksc0p3r.simplecounter.settings.SettingsManager

@Composable
fun SimpleCounterTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val manager = SettingsManager(context)
    val lightDarkToken by manager.lightDarkSettingFlow.collectAsState(initial = LightDarkSetting.SYSTEM.ordinal)
    val colorSettingToken by manager.colorSettingFlow.collectAsState(
        initial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ColorSetting.SYSTEM.ordinal
        else
            ColorSetting.PURPLE.ordinal
    )
    val oledModeToken by manager.oledModeSettingFlow.collectAsState(initial = false)
    val isDarkTheme = ((lightDarkToken == LightDarkSetting.SYSTEM.ordinal) && isSystemInDarkTheme()) || (lightDarkToken == LightDarkSetting.DARK.ordinal)
    val systemUIController = rememberSystemUiController()
    val selectedColorScheme = if (isDarkTheme)
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
    val colorScheme = if (!isDarkTheme) selectedColorScheme
    else
        darkColorScheme(
            primary = selectedColorScheme.primary,
            secondary = selectedColorScheme.secondary,
            secondaryContainer = selectedColorScheme.secondaryContainer,
            onSecondaryContainer = selectedColorScheme.onSecondaryContainer,
            tertiary = selectedColorScheme.tertiary,
            tertiaryContainer = selectedColorScheme.tertiaryContainer,
            onTertiaryContainer = selectedColorScheme.onTertiaryContainer,
            background = if (!oledModeToken) selectedColorScheme.background else Color(0xFF000000),
            surface = if (!oledModeToken) selectedColorScheme.surface else Color(0xFF000000),
            onSurface = selectedColorScheme.onSurface,
            surfaceVariant = selectedColorScheme.surfaceVariant,
            onSurfaceVariant = selectedColorScheme.onSurfaceVariant
        )
    SideEffect {
        systemUIController.setNavigationBarColor(colorScheme.background)
        systemUIController.setStatusBarColor(Color.Transparent, darkIcons = !isDarkTheme)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}