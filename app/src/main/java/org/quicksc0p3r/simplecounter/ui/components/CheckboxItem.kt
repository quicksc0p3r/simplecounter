package org.quicksc0p3r.simplecounter.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxItem(
    name: String,
    isToggled: Boolean,
    changeValue: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .toggleable(
                value = isToggled,
                onValueChange = changeValue,
                role = Role.Checkbox
            )
            .height(42.dp),
        leadingContent = {
            Checkbox(
                checked = isToggled,
                onCheckedChange = null
            )
        },
        headlineContent = { Text(name) }
    )
}