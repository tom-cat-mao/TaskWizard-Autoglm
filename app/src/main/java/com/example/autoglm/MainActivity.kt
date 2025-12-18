package com.example.autoglm

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.autoglm.manager.ShizukuManager
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {

    private lateinit var statusText: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.tv_status)
        imageView = findViewById(R.id.iv_preview)

        findViewById<Button>(R.id.btn_test_shell).setOnClickListener {
            testShellCommand()
        }
        
        findViewById<Button>(R.id.btn_test_screenshot).setOnClickListener {
            testScreenshot()
        }

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        if (ShizukuManager.checkPermission()) {
            statusText.text = "Status: Shizuku Granted"
        } else {
            statusText.text = "Status: Requesting Shizuku..."
            ShizukuManager.requestPermission(this)
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            statusText.text = "Status: Permission Granted"
        } else {
            statusText.text = "Status: Permission Denied"
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this)
        ShizukuManager.unbind()
    }

    private fun testShellCommand() {
        lifecycleScope.launch {
            try {
                statusText.text = "Executing Shell..."
                val service = ShizukuManager.bindService(this@MainActivity)
                val result = service.executeShellCommand("echo 'Hello AutoGLM'")
                Log.d("TEST", "Shell Output: $result")
                statusText.text = "Shell Output: ${result.trim()}"
            } catch (e: Exception) {
                Log.e("TEST", "Shell Failed", e)
                statusText.text = "Error: ${e.message}"
            }
        }
    }

    private fun testScreenshot() {
        lifecycleScope.launch {
            try {
                statusText.text = "Taking Screenshot..."
                val start = System.currentTimeMillis()
                
                // 1. 获取 Service
                val service = ShizukuManager.bindService(this@MainActivity)
                
                // 2. 截图 (返回 byte[])
                val bytes = service.takeScreenshot()
                val time = System.currentTimeMillis() - start
                
                // 3. 校验并显示
                if (bytes.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        statusText.text = "Success: ${bytes.size/1024}KB in ${time}ms"
                    } else {
                        statusText.text = "Error: Failed to decode bitmap"
                    }
                } else {
                    statusText.text = "Error: Screenshot returned empty bytes"
                }
                
            } catch (e: Exception) {
                Log.e("TEST", "Screenshot Failed", e)
                statusText.text = "Exception: ${e.message}"
            }
        }
    }
}
