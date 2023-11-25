package org.quicksc0p3r.simplecounter.ui.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.quicksc0p3r.simplecounter.CONTRIBUTORS
import org.quicksc0p3r.simplecounter.R

@Composable
fun ContributorsDialog(dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(
                onClick = dismiss,
                content = { Text(stringResource(R.string.close)) }
            )
        },
        title = { Text(stringResource(R.string.contributors)) },
        text = { Text(CONTRIBUTORS) }
    )
}