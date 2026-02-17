package myapplication.test.wattwisepro

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import myapplication.test.wattwisepro.api.RetrofitClient
import myapplication.test.wattwisepro.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize SessionManager
        SessionManager.init(this)
        
        setContentView(R.layout.settings)


        val buttonTips = findViewById<Button>(R.id.button_tips)
        val buttonHistory = findViewById<Button>(R.id.button_history)
        val buttonLogout = findViewById<Button>(R.id.button_logout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Initialize profile TextViews
        val tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileAddress = findViewById<TextView>(R.id.tvProfileAddress)
        val tvProfileHouseholdType = findViewById<TextView>(R.id.tvProfileHouseholdType)
        val tvProfileCity = findViewById<TextView>(R.id.tvProfileCity)
        val tvProfileSubdivision = findViewById<TextView>(R.id.tvProfileSubdivision)
        val tvProfilePhone = findViewById<TextView>(R.id.tvProfilePhone)

        // Load user profile
        loadUserProfile()

        bottomNavigationView.selectedItemId = R.id.settings_nav


        buttonTips.setOnClickListener {
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0) // disable animation
        }


        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0) // disable animation
        }

        // Logout button functionality
        buttonLogout.setOnClickListener {
            // Clear session
            SessionManager.logout()
            
            // Navigate to LoginActivity and clear activity stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }


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
                    // already here
                    true
                }
                else -> false
            }
        }
        
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

    private fun loadUserProfile() {
        val userId = SessionManager.getUserId()
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.apiService
        val call = apiService.getUserProfile(userId)

        call.enqueue(object : Callback<myapplication.test.wattwisepro.model.UserProfileResponse> {
            override fun onResponse(
                call: Call<myapplication.test.wattwisepro.model.UserProfileResponse>,
                response: Response<myapplication.test.wattwisepro.model.UserProfileResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val profileResponse = response.body()!!
                    if (profileResponse.success && profileResponse.data != null) {
                        val user = profileResponse.data!!
                        updateProfileUI(user)
                    } else {
                        showError("Failed to load profile: ${profileResponse.message ?: "Unknown error"}")
                    }
                } else {
                    // Fallback to SessionManager data if API fails
                    loadProfileFromSession()
                    showError("Could not fetch profile from server. Showing cached data.")
                }
            }

            override fun onFailure(
                call: Call<myapplication.test.wattwisepro.model.UserProfileResponse>,
                t: Throwable
            ) {
                // Fallback to SessionManager data if API fails
                loadProfileFromSession()
                showError("Network error. Showing cached data.")
            }
        })
    }

    private fun updateProfileUI(user: myapplication.test.wattwisepro.model.UserProfile) {
        findViewById<TextView>(R.id.tvProfileName).text = user.Name ?: "N/A"
        findViewById<TextView>(R.id.tvProfileEmail).text = user.Email ?: "N/A"
        findViewById<TextView>(R.id.tvProfileAddress).text = user.Address ?: "N/A"
        findViewById<TextView>(R.id.tvProfileHouseholdType).text = user.HouseholdType ?: "N/A"
        findViewById<TextView>(R.id.tvProfileCity).text = user.City ?: "N/A"
        findViewById<TextView>(R.id.tvProfileSubdivision).text = user.Subdivision ?: "N/A"
        findViewById<TextView>(R.id.tvProfilePhone).text = user.PhoneNumber ?: "N/A"
    }

    private fun loadProfileFromSession() {
        // Fallback: Use data from SessionManager (limited fields available)
        findViewById<TextView>(R.id.tvProfileName).text = SessionManager.getUserName() ?: "N/A"
        findViewById<TextView>(R.id.tvProfileEmail).text = SessionManager.getUserEmail() ?: "N/A"
        findViewById<TextView>(R.id.tvProfileAddress).text = "N/A"
        findViewById<TextView>(R.id.tvProfileHouseholdType).text = "N/A"
        findViewById<TextView>(R.id.tvProfileCity).text = "N/A"
        findViewById<TextView>(R.id.tvProfileSubdivision).text = "N/A"
        findViewById<TextView>(R.id.tvProfilePhone).text = "N/A"
    }

    private fun showError(message: String) {
        // Only show error if it's not a fallback scenario
        if (!message.contains("cached")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    
}
