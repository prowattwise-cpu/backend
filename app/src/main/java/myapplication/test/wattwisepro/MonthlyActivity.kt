package myapplication.test.wattwisepro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import myapplication.test.wattwisepro.api.RetrofitClient
import myapplication.test.wattwisepro.model.MonthlyUsageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthlyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.monthly)

        val backBtn: Button = findViewById(R.id.button5)
        backBtn.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // Load and display monthly usage
        loadMonthlyData()

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
    
    private fun loadMonthlyData() {
        // Get TextViews for each month (ordered: January to December)
        val kwhJanuary: TextView = findViewById(R.id.kwhJanuary)
        val kwhFebruary: TextView = findViewById(R.id.kwhFebruary)
        val kwhMarch: TextView = findViewById(R.id.kwhMarch)
        val kwhApril: TextView = findViewById(R.id.kwhApril)
        val kwhMay: TextView = findViewById(R.id.kwhMay)
        val kwhJune: TextView = findViewById(R.id.kwhJune)
        val kwhJuly: TextView = findViewById(R.id.kwhJuly)
        val kwhAugust: TextView = findViewById(R.id.kwhAugust)
        val kwhSeptember: TextView = findViewById(R.id.kwhSeptember)
        val kwhOctober: TextView = findViewById(R.id.kwhOctober)
        val kwhNovember: TextView = findViewById(R.id.kwhNovember)
        val kwhDecember: TextView = findViewById(R.id.kwhDecember)
        
        val textViews = arrayOf(
            kwhJanuary, kwhFebruary, kwhMarch, kwhApril, kwhMay, kwhJune,
            kwhJuly, kwhAugust, kwhSeptember, kwhOctober, kwhNovember, kwhDecember
        )
        
        // Set placeholder values initially
        textViews.forEach { it.text = "-----/kWh" }
        
        // Fetch data from API
        val apiService = RetrofitClient.apiService
        apiService.getMonthlyUsageYear().enqueue(object : Callback<MonthlyUsageResponse> {
            override fun onResponse(
                call: Call<MonthlyUsageResponse>,
                response: Response<MonthlyUsageResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val monthlyData = response.body()?.data
                    if (monthlyData != null && monthlyData.size == 12) {
                        // Map data to TextViews (January = index 0, December = index 11)
                        // Order: January, February, ..., December
                        for (i in monthlyData.indices) {
                            val usage = monthlyData[i]
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
                    val errorMsg = response.body()?.message ?: "Failed to load monthly usage"
                    showError(errorMsg)
                }
            }

            override fun onFailure(call: Call<MonthlyUsageResponse>, t: Throwable) {
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