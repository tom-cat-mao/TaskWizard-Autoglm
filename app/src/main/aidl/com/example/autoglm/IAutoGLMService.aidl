package com.example.autoglm;

interface IAutoGLMService {
    /**
     * 销毁服务（释放资源）
     */
    void destroy();

    /**
     * 执行 Shell 命令
     * @param command 例如 "input tap 500 500"
     * @return 命令的标准输出 (stdout)
     */
    String executeShellCommand(String command);

    /**
     * 获取屏幕截图
     * 直接返回图片字节数据，避免文件IO，提高速度
     * @return PNG 格式的字节数组
     */
    byte[] takeScreenshot();
    
    /**
     * 注入 Base64 文本 (通过 ADB Keyboard 广播)
     * @param base64Text Base64编码的文本
     */
    void injectInputBase64(String base64Text);
}
