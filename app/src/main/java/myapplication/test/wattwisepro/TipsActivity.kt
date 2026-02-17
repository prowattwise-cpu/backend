package myapplication.test.wattwisepro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity

class TipsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tips)

        // --- Back Button ---
        val backBtn: Button = findViewById(R.id.button5)
        backBtn.setOnClickListener {
            finish() // go back
            overridePendingTransition(0, 0) // 🚀 remove animation
        }

        // --- Bottom Navigation ---
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // ✅ Tips is under Settings section → highlight Settings tab
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
    
    override fun onBackPressed() {
        // Go back to SettingsActivity
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }
}
