package org.quicksc0p3r.simplecounter.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.settings.Setting
import org.quicksc0p3r.simplecounter.ui.components.RadioGroupMember

@Composable
fun SettingDialog(
    dismiss: () -> Unit,
    settingName: String,
    settings: List<Setting>,
    currentValue: Int,
    changeSetting: (Int) -> Unit
) {
    var state by remember { mutableIntStateOf(currentValue) }
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(settingName) },
        confirmButton = {
            TextButton(
                onClick = {
                    changeSetting(state)
                    dismiss()
                },
                content = { Text(stringResource(R.string.save)) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = dismiss,
                content = { Text(stringResource(R.string.cancel)) }
            )
        },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                settings.forEach {setting ->
                    RadioGroupMember(
                        name = setting.name,
                        isSelected = state == setting.value,
                        changeValue = { state = setting.value },
                        minSDK = setting.minSDK,
                        minSDKMessage = if (setting.minSDK != null)
                            stringResource(R.string.min_sdk_required, setting.minVer!!)
                        else null
                    )
                }
            }
        }
    )
}