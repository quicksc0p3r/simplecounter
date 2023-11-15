package org.quicksc0p3r.simplecounter.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.quicksc0p3r.simplecounter.BuildConfig
import org.quicksc0p3r.simplecounter.NavRoutes
import org.quicksc0p3r.simplecounter.R
import org.quicksc0p3r.simplecounter.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val annotatedString = buildAnnotatedString {
        var splitString = stringResource(R.string.credits).split('|')
        if (splitString.size < 3) splitString = "Made by quicksc0p3r and |contributors|".split('|')
        append(splitString[0])
        pushStringAnnotation(tag = "contributors", annotation = "")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(splitString[1])
        }
        pop()
        append(splitString[2])
    }
    var contributorsDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.about)) },
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
        }
    ) {padding ->
        if (contributorsDialogOpen)
            AlertDialog(
                onDismissRequest = { contributorsDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = { contributorsDialogOpen = false },
                        content = { Text(stringResource(R.string.close)) }
                    )
                },
                title = { Text(stringResource(R.string.contributors)) },
                text = {}
            )
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ElevatedCard(
                    modifier = Modifier
                        .size(
                            if (displayMetrics.heightPixels * displayMetrics.density <= 560) 100.dp
                            else 200.dp
                        )
                        .padding(2.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.about_icon),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = "Simple Counter",
                    style = Typography.displaySmall,
                    modifier = Modifier.padding(top = 15.dp)
                )
                Text(
                    text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                    style = Typography.bodyMedium,
                    modifier = Modifier.alpha(0.33F)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(
                    onClick = { uriHandler.openUri("https://www.apache.org/licenses/LICENSE-2.0") }
                ) {
                    Text(stringResource(R.string.license))
                }
                TextButton(
                    onClick = { uriHandler.openUri("https://github.com/quicksc0p3r/simplecounter") }
                ) {
                    Text("GitHub")
                }
                TextButton(
                    onClick = {}
                ) {
                    Text(stringResource(R.string.translation_help))
                }
                ClickableText(
                    text = annotatedString,
                    onClick = {offset ->
                        annotatedString.getStringAnnotations(tag = "contributors", start = offset, end = offset).firstOrNull()?.let {
                            contributorsDialogOpen = true
                        }
                    },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}