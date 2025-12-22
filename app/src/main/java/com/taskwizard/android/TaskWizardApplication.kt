package com.taskwizard.android

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log

/**
 * TaskWizardApplication - 应用程序类
 *
 * 负责管理应用级别的资源和生命周期
 *
 * 关键职责：
 * - 管理 TaskScope 的生命周期
 * - 管理广播接收器（Application 级别，不受 Activity 影响）
 * - 在应用退出时清理所有后台任务
 */
class TaskWizardApplication : Application() {

    companion object {
        private const val TAG = "TaskWizardApplication"

        // 单例引用，用于外部访问
        private var instance: TaskWizardApplication? = null

        fun getInstance(): TaskWizardApplication? = instance
    }

    // 广播接收器，用于接收停止任务信号
    private val stopTaskReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.taskwizard.android.ACTION_STOP_TASK") {
                Log.d(TAG, "Received stop task broadcast")
                // 通知 TaskScope 停止任务
                TaskScope.stopCurrentTask()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "Application created")

        // 注册广播接收器（Application 级别）
        val filter = IntentFilter("com.taskwizard.android.ACTION_STOP_TASK")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stopTaskReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(stopTaskReceiver, filter)
        }
        Log.d(TAG, "Stop task broadcast receiver registered at Application level")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "Application terminating")

        // 注销广播接收器
        try {
            unregisterReceiver(stopTaskReceiver)
            Log.d(TAG, "Stop task broadcast receiver unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unregister receiver", e)
        }

        // 取消所有任务
        TaskScope.cancelAll()
    }
}
