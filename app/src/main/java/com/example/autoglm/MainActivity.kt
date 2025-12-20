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
