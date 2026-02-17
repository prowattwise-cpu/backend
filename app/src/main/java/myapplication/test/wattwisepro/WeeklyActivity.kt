package myapplication.test.wattwisepro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import myapplication.test.wattwisepro.api.RetrofitClient
import myapplication.test.wattwisepro.model.WeeklyUsageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeeklyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weekly_main)

        val backBtn: Button = findViewById(R.id.button5)
        backBtn.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // Load and display weekly usage
        loadWeeklyData()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home_nav

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_nav -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.calcu_nav -> {
                    val intent = Intent(this, CalculatorActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
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
    }
    
    private fun loadWeeklyData() {
        // Get TextViews for each week (ordered: First, Second, Third, Fourth)
        val kwhFirstWeek: TextView = findViewById(R.id.kwhFirstWeek)
        val kwhSecondWeek: TextView = findViewById(R.id.kwhSecondWeek)
        val kwhThirdWeek: TextView = findViewById(R.id.kwhThirdWeek)
        val kwhFourthWeek: TextView = findViewById(R.id.kwhFourthWeek)
        
        val textViews = arrayOf(kwhFirstWeek, kwhSecondWeek, kwhThirdWeek, kwhFourthWeek)
        
        // Set placeholder values initially
        textViews.forEach { it.text = "-----/kWh" }
        
        // Fetch data from API
        val apiService = RetrofitClient.apiService
        apiService.getWeeklyUsageRecent().enqueue(object : Callback<WeeklyUsageResponse> {
            override fun onResponse(
                call: Call<WeeklyUsageResponse>,
                response: Response<WeeklyUsageResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val weeklyData = response.body()?.data
                    if (weeklyData != null && weeklyData.size == 4) {
                        // Map data to TextViews (most recent week first)
                        // Order: First (most recent), Second, Third, Fourth (oldest)
                        for (i in weeklyData.indices) {
                            val usage = weeklyData[i]
                            val energy = usage.total_energy
                            
                            // Format: Always show 3 decimal places (no rounding, preserve precision)
                            val displayText = String.format("%.3f/kWh", energy)
                            
                            textViews[i].text = displayText
                        }
                    } else {
                        // Data structure unexpected
                        showError("Invalid data format received")
                    }
                } else {
                    // API call failed
                    val errorMsg = response.body()?.message ?: "Failed to load weekly usage"
                    showError(errorMsg)
                }
            }

            override fun onFailure(call: Call<WeeklyUsageResponse>, t: Throwable) {
                // Network error
                showError("Network error: ${t.message}")
            }
        })
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onBackPressed() {
        // Go back to HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }
}

