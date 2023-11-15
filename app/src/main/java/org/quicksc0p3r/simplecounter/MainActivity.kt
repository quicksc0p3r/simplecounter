package org.quicksc0p3r.simplecounter

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.quicksc0p3r.simplecounter.db.CountersViewModel
import org.quicksc0p3r.simplecounter.db.CountersViewModelFactory
import org.quicksc0p3r.simplecounter.db.LabelsViewModel
import org.quicksc0p3r.simplecounter.db.LabelsViewModelFactory
import org.quicksc0p3r.simplecounter.ui.screens.About
import org.quicksc0p3r.simplecounter.ui.screens.FullscreenView
import org.quicksc0p3r.simplecounter.ui.screens.MainComposable
import org.quicksc0p3r.simplecounter.ui.screens.Settings
import org.quicksc0p3r.simplecounter.ui.theme.SimpleCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SimpleCounterTheme {
                // A surface container using the 'background' color from the Theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val countersViewModel: CountersViewModel = viewModel(
                            it, "CountersViewModel",
                            factory = CountersViewModelFactory(LocalContext.current.applicationContext as Application)
                        )
                        val labelsViewModel: LabelsViewModel = viewModel(
                            it, "LabelsViewModel",
                            factory = LabelsViewModelFactory(LocalContext.current.applicationContext as Application)
                        )
                        MainNavigation(countersViewModel, labelsViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainNavigation(countersViewModel: CountersViewModel, labelsViewModel: LabelsViewModel) {
    val navController = rememberNavController()
    var fullscreenCounterId by rememberSaveable { mutableIntStateOf(-1) }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.route
    ) {

        composable(NavRoutes.Main.route) {
            MainComposable(
                countersViewModel = countersViewModel,
                labelsViewModel = labelsViewModel,
                navController = navController,
                setFullscreenCounter = {counterId ->
                    fullscreenCounterId = counterId
                }
            )
        }
        composable(NavRoutes.Settings.route) {
            Settings(navController = navController)
        }
        composable(NavRoutes.About.route) {
            About(navController = navController)
        }
        composable(NavRoutes.FullscreenView.route) {
            FullscreenView(
                counterId = fullscreenCounterId,
                countersViewModel = countersViewModel,
                labelsViewModel = labelsViewModel,
                navController = navController
            )
        }
    }
}