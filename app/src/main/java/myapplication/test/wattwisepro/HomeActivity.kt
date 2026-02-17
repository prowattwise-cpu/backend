package myapplication.test.wattwisepro

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import myapplication.test.wattwisepro.utils.SessionManager
import myapplication.test.wattwisepro.api.RetrofitClient
import myapplication.test.wattwisepro.model.RawUsageLatestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.TextWatcher
import android.text.Editable
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    
    companion object {
        private const val CHANNEL_ID = "usage_threshold_notifications"
        private const val CHANNEL_NAME = "Usage Threshold Alerts"
        private const val NOTIFICATION_ID = 1001
        private const val PREFS_NAME = "WattWisePrefs"
        private const val KEY_THRESHOLD = "usage_threshold"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_NOTIFICATION_TIME = "last_notification_time"
        private const val PERMISSION_REQUEST_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_main)

        // Initialize SessionManager
        SessionManager.init(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.home_nav

        val dailyButton = findViewById<Button>(R.id.btnDaily)
        val weeklyButton = findViewById<Button>(R.id.btnWeekly)
        val monthlyButton = findViewById<Button>(R.id.btnMonthly)

        // ====== 🧭 NAVIGATION BUTTONS ======
        dailyButton.setOnClickListener {
            val intent = Intent(this, DailyActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        weeklyButton.setOnClickListener {
            val intent = Intent(this, WeeklyActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        monthlyButton.setOnClickListener {
            val intent = Intent(this, MonthlyActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // ====== ⚙️ BOTTOM NAVIGATION ======
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_nav -> true

                R.id.calcu_nav -> {
                    val intent = Intent(this, CalculatorActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.button_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.settings_nav -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                else -> false
            }
        }

        val tvVoltageValue: TextView = findViewById(R.id.tvVoltageValue)
        val tvPowerValue: TextView = findViewById(R.id.tvPowerValue)
        val tvEnergyValue: TextView = findViewById(R.id.tvEnergyValue)
        val tvCurrentValue: TextView = findViewById(R.id.tvCurrentValue)
        val tvLiveDate: TextView = findViewById(R.id.tvLiveDate)
        val etThreshold: EditText = findViewById(R.id.etThreshold)
        val tvThresholdStatus: TextView = findViewById(R.id.tvThresholdStatus)
        val switchNotification: Switch = findViewById(R.id.switchNotification)

        // Create notification channel
        createNotificationChannel()
        
        // Check if first launch and show permission dialog
        checkFirstLaunchAndRequestPermission()

        // Load saved preferences
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedThreshold = prefs.getFloat(KEY_THRESHOLD, 5000f)
        // Display threshold value, removing unnecessary decimal zeros
        etThreshold.setText(if (savedThreshold % 1 == 0f) savedThreshold.toInt().toString() else String.format("%.1f", savedThreshold))

        // Load saved notification toggle state
        val notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        switchNotification.isChecked = notificationsEnabled

        // Setup notification switch
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply()
            if (isChecked) {
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Update threshold when user changes the input - save immediately as user types
        etThreshold.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputText = s?.toString()?.trim() ?: ""
                if (inputText.isNotEmpty()) {
                    try {
                        val threshold = inputText.toFloat().coerceAtLeast(0f)
                        prefs.edit().putFloat(KEY_THRESHOLD, threshold).apply()
                    } catch (e: NumberFormatException) {
                        // Invalid input, ignore for now (will be handled on focus change)
                    }
                }
            }
        })
        
        // Also handle focus change for validation
        etThreshold.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // When user finishes editing, validate and save the threshold
                val inputText = etThreshold.text.toString().trim()
                if (inputText.isNotEmpty()) {
                    try {
                        val threshold = inputText.toFloat().coerceAtLeast(0f)
                        prefs.edit().putFloat(KEY_THRESHOLD, threshold).apply()
                        updateThresholdStatus(0f, threshold)
                    } catch (e: NumberFormatException) {
                        // Invalid input, reset to saved value
                        val saved = prefs.getFloat(KEY_THRESHOLD, 5000f)
                        etThreshold.setText(if (saved % 1 == 0f) saved.toInt().toString() else String.format("%.1f", saved))
                        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Empty input, reset to saved value
                    val saved = prefs.getFloat(KEY_THRESHOLD, 5000f)
                    etThreshold.setText(if (saved % 1 == 0f) saved.toInt().toString() else String.format("%.1f", saved))
                }
            }
        }

        val today = java.text.SimpleDateFormat("MMMM dd", java.util.Locale.getDefault())
            .format(java.util.Date())
        tvLiveDate.text = "Live usage: $today"

        val handler = android.os.Handler()
        // Load last notification time from SharedPreferences (persists across app restarts)
        var lastNotificationTime: Long = prefs.getLong(KEY_LAST_NOTIFICATION_TIME, 0L)
        val COOLDOWN_PERIOD_MS = 5 * 60 * 1000L // 5 minutes in milliseconds
        
        // Track previous accumulated power to calculate incremental (new reading from hardware)
        // Store in SharedPreferences to persist across app restarts
        val KEY_PREVIOUS_POWER = "previous_accumulated_power"
        var previousPower: Float = prefs.getFloat(KEY_PREVIOUS_POWER, -1f) // -1 means no previous value
        
        // Track last valid incremental power to display when no new data arrives
        // Store in SharedPreferences to persist across app restarts
        val KEY_LAST_VALID_INCREMENTAL = "last_valid_incremental_power"
        var lastValidIncremental: Float = prefs.getFloat(KEY_LAST_VALID_INCREMENTAL, 0f)
        
        // Function to fetch and update live usage data
        fun fetchLiveUsage() {
            val apiService = RetrofitClient.apiService
            apiService.getLatestRawUsage().enqueue(object : Callback<RawUsageLatestResponse> {
                override fun onResponse(
                    call: Call<RawUsageLatestResponse>,
                    response: Response<RawUsageLatestResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()?.data
                        if (data != null) {
                            // Parse values (they come as strings from database)
                            val voltage = try { data.voltage.toFloat() } catch (e: Exception) { 0.0f }
                            val current = try { data.current.toFloat() } catch (e: Exception) { 0.0f }
                            // Backend may return accumulated power (if cache is not available)
                            // Calculate incremental power in the app: current - previous = new reading
                            val currentPower = try { data.power.toFloat() } catch (e: Exception) { 0.0f }
                            val energy = try { data.energy.toFloat() } catch (e: Exception) { 0.0f }
                            
                            // Check if backend returned 0.000 (stale data/hardware off)
                            val isStaleData = (currentPower == 0f && voltage == 0f && current == 0f)
                            
                            // Calculate incremental power (new reading from hardware)
                            // This shows only what was added since the last reading, not the accumulated total
                            val calculatedIncremental = if (previousPower >= 0f && currentPower > 0f) {
                                // Calculate difference: current accumulated - previous accumulated = new reading
                                val incremental = currentPower - previousPower
                                // Ensure non-negative (handle edge cases like hardware reset)
                                Math.max(0f, incremental)
                            } else {
                                // First reading or no previous value - show 0 (no new reading yet)
                                0f
                            }
                            
                            // Determine what to display:
                            // - If stale data (all zeros): show 0.000
                            // - If incremental > 0: show the new incremental value
                            // - If incremental = 0 but hardware is on (voltage/current > 0): show last valid incremental
                            // - If first reading: show 0.000
                            val displayPower = when {
                                isStaleData -> {
                                    // Backend explicitly returned 0.000 (stale data/hardware off)
                                    0f
                                }
                                calculatedIncremental > 0f -> {
                                    // New data arrived - use the calculated incremental
                                    calculatedIncremental
                                }
                                voltage > 0f || current > 0f -> {
                                    // No new data yet, but hardware is still on - show last valid incremental
                                    lastValidIncremental
                                }
                                else -> {
                                    // First reading or no data - show 0
                                    0f
                                }
                            }
                            
                            // Update last valid incremental if we got a new reading
                            if (calculatedIncremental > 0f) {
                                lastValidIncremental = calculatedIncremental
                                prefs.edit().putFloat(KEY_LAST_VALID_INCREMENTAL, lastValidIncremental).apply()
                            }
                            
                            // Update previous power for next calculation (only if we got valid data)
                            // Don't update if currentPower is 0 (stale data from backend)
                            if (currentPower > 0f) {
                                previousPower = currentPower
                                prefs.edit().putFloat(KEY_PREVIOUS_POWER, previousPower).apply()
                            }
                            
                            // Update UI with real data
                            // Power shows latest instantaneous reading (new reading from hardware, not accumulated)
                            tvVoltageValue.text = String.format("%.3f", voltage)
                            tvPowerValue.text = String.format("%.3f", displayPower)
                            tvEnergyValue.text = String.format("%.3f", energy)
                            tvCurrentValue.text = String.format("%.3f", current)
                            
                            // Check threshold and send notification if needed (using Watts)
                            val thresholdText = etThreshold.text.toString().trim()
                            val threshold = if (thresholdText.isNotEmpty()) {
                                try {
                                    thresholdText.toFloat().coerceAtLeast(0f)
                                } catch (e: NumberFormatException) {
                                    prefs.getFloat(KEY_THRESHOLD, 5000f)
                                }
                            } else {
                                prefs.getFloat(KEY_THRESHOLD, 5000f)
                            }
                            
                            // Update threshold status display (use display power for threshold check)
                            updateThresholdStatus(displayPower, threshold)
                            
                            // Check if threshold is reached with 5-minute cooldown
                            // Only send notification if notifications are enabled
                            val notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
                            if (threshold > 0 && notificationsEnabled) {
                                val currentTime = System.currentTimeMillis()
                                val timeSinceLastNotification = currentTime - lastNotificationTime
                                
                                if (displayPower >= threshold) {
                                    // Usage is above threshold
                                    // Send notification if:
                                    // 1. No notification was sent before (lastNotificationTime == 0), OR
                                    // 2. 5 minutes have passed since last notification
                                    if (lastNotificationTime == 0L || timeSinceLastNotification >= COOLDOWN_PERIOD_MS) {
                                        sendThresholdNotification(displayPower, threshold)
                                        lastNotificationTime = currentTime
                                        // Save to SharedPreferences to persist across app restarts
                                        prefs.edit().putLong(KEY_LAST_NOTIFICATION_TIME, lastNotificationTime).apply()
                                    }
                                } else if (displayPower < threshold) {
                                    // Usage is below threshold - reset cooldown
                                    lastNotificationTime = 0L
                                    prefs.edit().putLong(KEY_LAST_NOTIFICATION_TIME, 0L).apply()
                                }
                            } else if (displayPower < threshold) {
                                // Reset cooldown when below threshold
                                lastNotificationTime = 0L
                                prefs.edit().putLong(KEY_LAST_NOTIFICATION_TIME, 0L).apply()
                            }
                        } else {
                            // No data available - show zeros with three decimal places
                            tvVoltageValue.text = "0.000"
                            tvPowerValue.text = "0.000"
                            tvEnergyValue.text = "0.000"
                            tvCurrentValue.text = "0.000"
                        }
                    } else {
                        // API call failed - keep last values or show zeros
                        // Don't update UI, just log error
                    }
                }

                override fun onFailure(call: Call<RawUsageLatestResponse>, t: Throwable) {
                    // Network error - keep last displayed values
                    // Don't show error to user to avoid spam
                }
            })
        }
        
        // Initial fetch
        fetchLiveUsage()
        
        // Poll every 5 seconds for live updates
        val updateRunnable = object : Runnable {
            override fun run() {
                fetchLiveUsage()
                handler.postDelayed(this, 5000) // Poll every 5 seconds
            }
        }
        handler.postDelayed(updateRunnable, 5000) // Start polling after 5 seconds
        
        // Handle back button press with exit confirmation dialog
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }
    
    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Do you really want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity() // Exit the app
            }
            .setNegativeButton("No", null)
            .setCancelable(false)
            .show()
    }
    
    private fun checkFirstLaunchAndRequestPermission() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        
        if (isFirstLaunch) {
            // Mark that we've shown the dialog
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            
            // Show permission dialog for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                    showNotificationPermissionDialog()
                }
            }
        } else {
            // Not first launch, just request permission silently if needed
            requestNotificationPermission()
        }
    }
    
    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Notifications")
            .setMessage("WattWisePro needs notification permission to alert you when your energy usage exceeds the threshold you set. This helps you monitor and control your energy consumption effectively.\n\nWould you like to enable notifications?")
            .setPositiveButton("Allow") { _, _ ->
                // Request permission when user clicks Allow
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Not Now") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "You can enable notifications later in Settings", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Don't show dialog again, just request silently
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied. You can enable it in Settings", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when energy usage exceeds the set threshold"
                enableVibration(true)
                enableLights(true)
                setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun sendThresholdNotification(currentUsage: Float, threshold: Float) {
        val notificationManager = NotificationManagerCompat.from(this)
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Threshold Usage Limit is Over")
            .setContentText("Current usage: ${String.format("%.3f", currentUsage)} W has reached the threshold of ${String.format("%.3f", threshold)} W")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun updateThresholdStatus(currentUsage: Float, threshold: Float) {
        val tvThresholdStatus: TextView = findViewById(R.id.tvThresholdStatus)
        if (threshold > 0) {
            // Compare Watts usage with Watts threshold
            val statusText = if (currentUsage >= threshold) {
                "⚠️ Threshold Exceeded! Current: ${String.format("%.3f", currentUsage)} W / Limit: ${String.format("%.3f", threshold)} W"
            } else {
                "Current: ${String.format("%.3f", currentUsage)} W / Limit: ${String.format("%.3f", threshold)} W"
            }
            tvThresholdStatus.text = statusText
        } else {
            tvThresholdStatus.text = "Current: ${String.format("%.3f", currentUsage)} W / Limit: Not Set"
        }
    }
    
}

