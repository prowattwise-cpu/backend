package myapplication.test.wattwisepro

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import myapplication.test.wattwisepro.api.RetrofitClient
import myapplication.test.wattwisepro.model.DailyUsageDateResponse
import myapplication.test.wattwisepro.model.MonthlyUsageCurrentResponse
import myapplication.test.wattwisepro.model.YearTotalUsageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    // Calendar and Usage
    private lateinit var calendarView: CalendarView
    private lateinit var tvWholeMonthUsage: TextView
    private lateinit var tvWholeYearUsage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)

        val backBtn: Button = findViewById(R.id.btn_back)
        backBtn.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // --- Calendar and Usage ---
        calendarView = findViewById(R.id.calendarView)
        tvWholeMonthUsage = findViewById(R.id.tvWholeMonthUsage)
        tvWholeYearUsage = findViewById(R.id.tvWholeYearUsage)

        // Set calendar to current date (today)
        val today = Calendar.getInstance()
        calendarView.date = today.timeInMillis

        // Load whole month and whole year usage on activity start
        loadWholeMonthUsage()
        loadWholeYearUsage()

        // --- Calendar Date Change Listener ---
        // This listener fires when user clicks a date
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            
            // Show popup with usage for the selected date
            showDateUsagePopup(selectedDate)
        }

        // --- Bottom Navigation ---
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.settings_nav

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

    // --- Load Whole Month Usage ---
    private fun loadWholeMonthUsage() {
        val apiService = RetrofitClient.apiService
        apiService.getMonthlyUsageCurrent().enqueue(object : Callback<MonthlyUsageCurrentResponse> {
            override fun onResponse(
                call: Call<MonthlyUsageCurrentResponse>,
                response: Response<MonthlyUsageCurrentResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        val energy = data.total_energy
                        // Display value even if 0 or partial data
                        tvWholeMonthUsage.text = String.format("%.3f/kWh", energy)
                    } else {
                        tvWholeMonthUsage.text = "0.000/kWh"
                    }
                } else {
                    tvWholeMonthUsage.text = "0.000/kWh"
                }
            }

            override fun onFailure(call: Call<MonthlyUsageCurrentResponse>, t: Throwable) {
                tvWholeMonthUsage.text = "0.000/kWh"
            }
        })
    }

    // --- Load Whole Year Usage ---
    private fun loadWholeYearUsage() {
        val apiService = RetrofitClient.apiService
        apiService.getYearTotalUsage().enqueue(object : Callback<YearTotalUsageResponse> {
            override fun onResponse(
                call: Call<YearTotalUsageResponse>,
                response: Response<YearTotalUsageResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        val energy = data.total_energy
                        // Display value even if 0 or partial data
                        tvWholeYearUsage.text = String.format("%.3f/kWh", energy)
                    } else {
                        tvWholeYearUsage.text = "0.000/kWh"
                    }
                } else {
                    tvWholeYearUsage.text = "0.000/kWh"
                }
            }

            override fun onFailure(call: Call<YearTotalUsageResponse>, t: Throwable) {
                tvWholeYearUsage.text = "0.000/kWh"
            }
        })
    }

    // --- Show Date Usage Popup ---
    private fun showDateUsagePopup(selectedDate: Calendar) {
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val dateString = sdf.format(selectedDate.time)
        
        // Format date as YYYY-MM-DD for API call
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateForApi = dateFormat.format(selectedDate.time)
        
        // Fetch usage data for the selected date
        val apiService = RetrofitClient.apiService
        apiService.getDailyUsageByDate(dateForApi).enqueue(object : Callback<DailyUsageDateResponse> {
            override fun onResponse(
                call: Call<DailyUsageDateResponse>,
                response: Response<DailyUsageDateResponse>
            ) {
                val energyUsage: String
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        // Always show 3 decimal places (no rounding, preserve precision)
                        energyUsage = String.format("%.3f/kWh", data.total_energy)
                    } else {
                        energyUsage = "0.000/kWh"
                    }
                } else {
                    energyUsage = "0.000/kWh"
                }
                
                // Show popup with usage data
                AlertDialog.Builder(this@HistoryActivity)
                    .setTitle("Usage for $dateString")
                    .setMessage("Energy Usage: $energyUsage")
                    .setPositiveButton("OK", null)
                    .show()
            }

            override fun onFailure(call: Call<DailyUsageDateResponse>, t: Throwable) {
                // Show popup with error message (show 0.000 instead of -----)
                AlertDialog.Builder(this@HistoryActivity)
                    .setTitle("Usage for $dateString")
                    .setMessage("Energy Usage: 0.000/kWh")
                    .setPositiveButton("OK", null)
                    .show()
            }
        })
    }
    
    override fun onBackPressed() {
        // Go back to SettingsActivity
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }
}
