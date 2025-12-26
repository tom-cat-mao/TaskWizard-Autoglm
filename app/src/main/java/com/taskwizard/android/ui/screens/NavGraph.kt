package com.taskwizard.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.taskwizard.android.ui.viewmodel.HistoryViewModel
import com.taskwizard.android.ui.viewmodel.HistoryViewModelFactory
import com.taskwizard.android.ui.viewmodel.MainViewModel

/**
 * 导航路由定义
 */
object NavRoutes {
    const val MAIN = "main"
    const val SETTINGS = "settings"
    const val HISTORY = "history"
}

/**
 * 应用导航图
 *
 * @param navController 导航控制器
 * @param viewModel 共享的ViewModel实例
 * @param modifier 修饰符
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.MAIN,
        modifier = modifier,
        enterTransition = {
            // 从右向左滑入
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            // 向左滑出并淡出
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            // 从左向右滑入
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            // 向右滑出
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // 主页面
        composable(NavRoutes.MAIN) {
            MainScreen(
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                },
                onNavigateToHistory = {
                    navController.navigate(NavRoutes.HISTORY)
                },
                viewModel = viewModel
            )
        }

        // 设置页面
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        // 历史记录页面
        composable(NavRoutes.HISTORY) {
            val historyViewModel = androidx.lifecycle.viewmodel.compose.viewModel<HistoryViewModel>(
                factory = HistoryViewModelFactory(
                    // Get Application from LocalContext in the composable
                    androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
                )
            )
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = historyViewModel
            )
        }
    }
}
