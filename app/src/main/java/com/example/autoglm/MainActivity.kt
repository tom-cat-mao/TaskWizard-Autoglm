package com.example.autoglm

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.autoglm.api.ApiClient
import com.example.autoglm.core.ActionExecutor
import com.example.autoglm.core.AgentCore
import com.example.autoglm.manager.ShizukuManager
import com.example.autoglm.utils.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {

    private lateinit var statusText: TextView
    private lateinit var imageView: ImageView
    
    private lateinit var etApiKey: EditText
    private lateinit var etBaseUrl: EditText
    private lateinit var etModel: EditText
    private lateinit var etTask: EditText
    
    // Components
    private val agentCore = AgentCore()
    private var actionExecutor: ActionExecutor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init Utils
        SettingsManager.init(this)

        // Bind Views
        statusText = findViewById(R.id.tv_status)
        imageView = findViewById(R.id.iv_preview)
        etApiKey = findViewById(R.id.et_api_key)
        etBaseUrl = findViewById(R.id.et_base_url)
        etModel = findViewById(R.id.et_model)
        etTask = findViewById(R.id.et_task)

        // Load Settings
        etApiKey.setText(SettingsManager.apiKey)
        etBaseUrl.setText(SettingsManager.baseUrl)
        etModel.setText(SettingsManager.model)

        // Setup Clear Buttons
        setupClearButton(R.id.btn_clear_api_key, etApiKey)
        setupClearButton(R.id.btn_clear_base_url, etBaseUrl)
        setupClearButton(R.id.btn_clear_model, etModel)
        setupClearButton(R.id.btn_clear_task, etTask)

        // Setup Main Buttons
        findViewById<Button>(R.id.btn_save_settings).setOnClickListener {
            saveSettings()
        }

        findViewById<Button>(R.id.btn_step).setOnClickListener {
            runOneStep()
        }

        checkAndRequestPermission()
    }

    private fun setupClearButton(btnId: Int, targetEditText: EditText) {
        findViewById<Button>(btnId).setOnClickListener {
            targetEditText.setText("")
        }
    }

    private fun saveSettings() {
        val apiKey = etApiKey.text.toString().trim()
        val baseUrl = etBaseUrl.text.toString().trim()
        val model = etModel.text.toString().trim()
        
        SettingsManager.apiKey = apiKey
        SettingsManager.baseUrl = baseUrl
        SettingsManager.model = model
        
        // Re-init API Client
        ApiClient.init(baseUrl, apiKey)
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
    }

    private fun checkAndRequestPermission() {
        if (ShizukuManager.checkPermission()) {
            statusText.text = "Status: Ready (Shizuku Granted)"
            initExecutor()
        } else {
            statusText.text = "Status: Requesting Shizuku..."
            ShizukuManager.requestPermission(this)
        }
    }
    
    private fun initExecutor() {
        // Initialize executor only when service is bound (in run step) or pre-bind here
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            statusText.text = "Status: Ready"
        } else {
            statusText.text = "Status: Permission Denied"
        }
    }

    private fun runOneStep() {
        val task = etTask.text.toString().trim()
        if (task.isEmpty()) {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (SettingsManager.apiKey.isEmpty()) {
             Toast.makeText(this, "Please set API Key", Toast.LENGTH_SHORT).show()
             return
        }

        statusText.text = "Status: Capturing Screenshot..."
        
        lifecycleScope.launch {
            try {
                // 1. Bind Service
                val service = ShizukuManager.bindService(this@MainActivity)
                
                // 2. Initialize Executor if needed
                if (actionExecutor == null) {
                    val metrics = resources.displayMetrics
                    actionExecutor = ActionExecutor(this@MainActivity, service, metrics.widthPixels, metrics.heightPixels)
                }

                // 3. Take Screenshot
                val bytes = service.takeScreenshot()
                if (bytes.isEmpty()) {
                    statusText.text = "Error: Screenshot failed"
                    return@launch
                }

                // Update UI Preview
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageView.setImageBitmap(bitmap)
                
                statusText.text = "Status: Thinking (API Call)..."

                // 4. Agent Core Step (Network IO)
                withContext(Dispatchers.IO) {
                    // Start session if it's the first step (simple logic for now)
                    // For now, always refresh task context for "One Step" button to act as "Start/Continue"
                    // But if history is not empty, maybe we shouldn't? 
                    // Let's assume user wants to RESTART if they click One Step manually and task text changed.
                    // For now: Just start session every time to be safe in this debug mode.
                    agentCore.startSession(task) 
                    
                    val action = agentCore.step(bytes)
                    
                    withContext(Dispatchers.Main) {
                        if (action != null) {
                            val think = agentCore.lastThink ?: "No thought"
                            statusText.text = "Think: $think\nAction: ${action.action} ${action.location ?: ""}"
                            // 5. Execute
                            actionExecutor?.execute(action)
                        } else {
                            statusText.text = "Status: No Action or Error (See Log)"
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("MainActivity", "Step Failed", e)
                statusText.text = "Error: ${e.message}"
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this)
        ShizukuManager.unbind()
    }
}
