package com.example.autoglm.config

object AppMap {
    // Ported from phone_agent/config/apps.py
    val PACKAGES = mapOf(
        // Social & Messaging
        "微信" to "com.tencent.mm",
        "QQ" to "com.tencent.mobileqq",
        "微博" to "com.sina.weibo",
        "WeChat" to "com.tencent.mm",
        "wechat" to "com.tencent.mm",
        
        // Video
        "bilibili" to "tv.danmaku.bili",
        "哔哩哔哩" to "tv.danmaku.bili",
        "抖音" to "com.ss.android.ugc.aweme",
        "快手" to "com.smile.gifmaker",
        "YouTube" to "com.google.android.youtube",
        
        // Shopping
        "淘宝" to "com.taobao.taobao",
        "京东" to "com.jingdong.app.mall",
        "拼多多" to "com.xunmeng.pinduoduo",
        "美团" to "com.sankuai.meituan",
        "饿了么" to "me.ele",
        
        // Tools
        "Settings" to "com.android.settings",
        "设置" to "com.android.settings",
        "Chrome" to "com.android.chrome",
        "Browser" to "com.android.chrome",
        "Camera" to "com.android.camera2",
        "相机" to "com.android.camera2",
        "Clock" to "com.android.deskclock",
        "时钟" to "com.android.deskclock"
        // Add more from original list if needed
    )
    
    /**
     * 根据应用名获取包名
     * @param appName 应用名（支持大小写不敏感匹配）
     * @return 包名，未找到返回 null
     */
    fun getPackageName(appName: String): String? {
        // 精确匹配
        PACKAGES[appName]?.let { return it }
        
        // 大小写不敏感匹配
        PACKAGES.entries.find { it.key.equals(appName, ignoreCase = true) }?.let { 
            return it.value 
        }
        
        return null
    }
    
    /**
     * 根据包名反向查询应用名
     * @param packageName 包名
     * @return 应用名，未找到返回 null
     */
    fun getAppName(packageName: String): String? {
        return PACKAGES.entries.find { it.value == packageName }?.key
    }
}
