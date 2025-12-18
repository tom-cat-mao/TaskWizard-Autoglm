# Phase 1: Shizuku Infrastructure

## 1. 编译与安装
1. 使用 Android Studio 打开项目。
2. 确保手机已安装 **Shizuku** App (Google Play 或 Github 下载)。
3. 确保手机已安装 **ADB Keyboard** (并启用)。
4. 运行本 App。

## 2. 授权测试
1. 打开 App，如果未授权，应弹出 Shizuku 授权请求。
2. 在 Shizuku App 中确认授权。

## 3. 功能验证 (Unit Test / Manual)

### 验证 1: Shell 命令执行
在 MainActivity 中调用：
```kotlin
lifecycleScope.launch {
    try {
        val service = ShizukuManager.bindService(this@MainActivity)
        val result = service.executeShellCommand("echo 'Hello AutoGLM'")
        Log.d("TEST", "Output: $result") // 应输出 "Hello AutoGLM"
    } catch (e: Exception) {
        Log.e("TEST", "Bind failed", e)
    }
}
```

### 验证 2: 截屏性能
```kotlin
val start = System.currentTimeMillis()
val bytes = service.takeScreenshot()
val time = System.currentTimeMillis() - start
Log.d("TEST", "Screenshot size: ${bytes.size}, Time: ${time}ms")
// 预期：Time 应在 500ms 以内，bytes.size > 0
```

### 验证 3: 模拟点击
```kotlin
// 点击屏幕中心 (请确保该位置无危险按钮)
service.executeShellCommand("input tap 500 1000")
```

## 4. 常见问题
*   **Binder Died**: Shizuku 进程可能被系统查杀。需要在 `ShizukuManager` 中处理重连逻辑（将在 Phase 3 完善）。
*   **Permission Denied**: 确保 `checkSelfPermission` 返回 GRANTED。
