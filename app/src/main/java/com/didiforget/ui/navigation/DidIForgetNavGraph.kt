package com.didiforget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.didiforget.di.AppContainer
import com.didiforget.di.ViewModelFactory
import com.didiforget.ui.activity.ActivityScreen
import com.didiforget.ui.activity.ObjectSelectionScreen
import com.didiforget.ui.checklist.ChecklistScreen
import com.didiforget.ui.home.HomeScreen
import com.didiforget.viewmodel.ActivityViewModel
import com.didiforget.viewmodel.ChecklistViewModel
import com.didiforget.viewmodel.HomeViewModel

/** Rutas de navegación, centralizadas para no repetir strings mágicos. */
private object Routes {
    const val HOME = "home"
    const val ACTIVITY = "activity"
    const val SELECTION = "seleccion"
    const val CHECKLIST = "checklist/{activityId}"

    fun checklist(activityId: Long) = "checklist/$activityId"
}

@Composable
fun DidIForgetNavGraph(container: AppContainer) {
    val navController = rememberSwipeDismissableNavController()
    val factory = ViewModelFactory(container)
    // Vuelve a Inicio dejándolo como única entrada del back stack (no queda
    // ninguna checklist/actividad apilada detrás).
    val goHome: () -> Unit = { navController.popBackStack(Routes.HOME, inclusive = false) }

    SwipeDismissableNavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = homeViewModel,
                onActivityClick = { activity -> navController.navigate(Routes.checklist(activity.id)) },
                onNewActivityClick = { navController.navigate(Routes.ACTIVITY) }
            )
        }

        composable(Routes.ACTIVITY) {
            val activityViewModel: ActivityViewModel = viewModel(factory = factory)
            ActivityScreen(
                viewModel = activityViewModel,
                onContinue = { navController.navigate(Routes.SELECTION) }
            )
        }

        composable(Routes.SELECTION) {
            // Comparte la misma instancia de ActivityViewModel que "activity"
            // (mismo ViewModelStoreOwner) para que la lista generada/agregada
            // ahí siga disponible acá, sin volver a pedirla ni duplicarla.
            val activityEntry = navController.getBackStackEntry(Routes.ACTIVITY)
            val activityViewModel: ActivityViewModel = viewModel(factory = factory, viewModelStoreOwner = activityEntry)
            ObjectSelectionScreen(
                viewModel = activityViewModel,
                onSaved = { savedActivity ->
                    navController.navigate(Routes.checklist(savedActivity.id)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.CHECKLIST) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId")?.toLongOrNull() ?: 0L
            val checklistViewModel: ChecklistViewModel = viewModel(factory = factory)
            ChecklistScreen(
                activityId = activityId,
                viewModel = checklistViewModel,
                onGoHome = goHome,
                onActivityDeleted = goHome
            )
        }
    }
}
