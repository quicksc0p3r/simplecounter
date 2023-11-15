package org.quicksc0p3r.simplecounter.ui.components

import android.os.Build
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RadioGroupMember(
    name: String,
    isSelected: Boolean,
    changeValue: () -> Unit,
    minSDK: Int? = null,
    minSDKMessage: String? = null
) {
    if (minSDK == null || Build.VERSION.SDK_INT >= minSDK)
        ListItem(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .selectable(
                    selected = isSelected,
                    onClick = changeValue,
                    role = Role.RadioButton
                )
                .height(42.dp),
            leadingContent = {
                RadioButton(
                    selected = isSelected,
                    onClick = null
                )
            },
            headlineContent = { Text(text = name) }
        )
    else
        ListItem(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .selectable(
                    selected = false,
                    onClick = {},
                    enabled = false,
                    role = Role.RadioButton
                )
                .height(60.dp),
            leadingContent = {
                RadioButton(
                    selected = false,
                    onClick = null,
                    enabled = false
                )
            },
            headlineContent = { Text(text = name, color = Color.Gray) },
            supportingContent = {
                if (minSDKMessage != null) {
                    Text(text = minSDKMessage, color = Color.Gray)
                }
            }
        )
}