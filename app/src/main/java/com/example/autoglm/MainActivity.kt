package com.example.autoglm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.autoglm.ui.screens.AppNavGraph
import com.example.autoglm.ui.theme.AutoGLMTheme
import com.example.autoglm.ui.viewmodel.MainViewModel

/**
 * MainActivity - Compose版本
 *
 * 阶段3：集成Navigation Compose导航系统
 * - 使用NavHost管理页面导航
 * - 支持主页面和设置页面切换
 * - 流畅的转场动画
 * - 响应主题变化
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 隐藏ActionBar（如果存在）
        actionBar?.hide()

        setContent {
            // 创建ViewModel
            val viewModel: MainViewModel = viewModel()
            val state by viewModel.state.collectAsState()

            // 阶段2新增：监听finish信号
            val shouldFinish by viewModel.shouldFinishActivity.collectAsState()

            // 阶段3新增：检查是否从小窗返回
            val fromOverlay = intent?.getBooleanExtra("FROM_OVERLAY", false) ?: false

            // 阶段3新增：处理从小窗返回的放大动画
            androidx.compose.runtime.LaunchedEffect(fromOverlay) {
                if (fromOverlay) {
                    // 设置放大动画状态
                    viewModel.setAnimatingFromOverlay(true)
                    // 等待动画完成
                    kotlinx.coroutines.delay(350)
                    // 重置动画状态
                    viewModel.setAnimatingFromOverlay(false)
                }
            }

            // 阶段2新增：处理finish逻辑
            androidx.compose.runtime.LaunchedEffect(shouldFinish) {
                if (shouldFinish) {
                    // 执行finish
                    finish()
                    // 设置透明退出动画（保持可见，让Compose动画完成）
                    overridePendingTransition(0, R.anim.transparent)
                    // 重置finish标志
                    viewModel.resetFinishActivityFlag()
                }
            }

            // 创建导航控制器
            val navController = rememberNavController()

            // 应用主题（响应ViewModel状态变化）
            AutoGLMTheme(
                themeMode = state.themeMode,
                pureBlackEnabled = state.pureBlackEnabled
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 导航图（传递共享的ViewModel）
                    AppNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
