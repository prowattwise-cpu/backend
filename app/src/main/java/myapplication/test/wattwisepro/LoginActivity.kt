package myapplication.test.wattwisepro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import myapplication.test.wattwisepro.api.RetrofitClient
import myapplication.test.wattwisepro.model.LoginRequest
import myapplication.test.wattwisepro.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize SessionManager
        SessionManager.init(this)
        
        // Check if user is already logged in
        if (SessionManager.isLoggedIn()) {
            // User is already logged in, redirect to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("userId", SessionManager.getUserId())
            intent.putExtra("userName", SessionManager.getUserName())
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
            return
        }
        
        setContentView(R.layout.login_main)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        signupBtn = findViewById(R.id.signup_btn)
        
        // Create progress bar programmatically if not in layout
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        signupBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }

        loginBtn.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        // Validate inputs
        if (!validateInputs(email, password)) {
            return
        }

        // Show loading
        loginBtn.isEnabled = false
        progressBar.visibility = View.VISIBLE

        // Create login request
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        // Make API call
        RetrofitClient.apiService.login(loginRequest)
            .enqueue(object : Callback<myapplication.test.wattwisepro.model.LoginResponse> {
                override fun onResponse(
                    call: Call<myapplication.test.wattwisepro.model.LoginResponse>,
                    response: Response<myapplication.test.wattwisepro.model.LoginResponse>
                ) {
                    loginBtn.isEnabled = true
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        if (loginResponse.success && loginResponse.user != null) {
                            // Save session
                            SessionManager.saveSession(loginResponse.user)
                            
                            Toast.makeText(
                                this@LoginActivity,
                                "Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            
                            // Navigate to home page
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            // Optionally pass user data
                            intent.putExtra("userId", loginResponse.user.userId)
                            intent.putExtra("userName", loginResponse.user.name)
                            startActivity(intent)
                            finish()
                            overridePendingTransition(0, 0)
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                loginResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle error response - try to parse error body
                        var errorMessage = "Login failed. Please try again."
                        try {
                            val errorBody = response.errorBody()?.string()
                            Log.e("LoginActivity", "Error response: $errorBody")
                            
                            // Try to parse error message from response
                            if (errorBody != null && errorBody.contains("\"message\"")) {
                                // Simple extraction of message (or use proper JSON parsing)
                                val messageMatch = Regex("\"message\"\\s*:\\s*\"([^\"]+)\"").find(errorBody)
                                if (messageMatch != null) {
                                    errorMessage = messageMatch.groupValues[1]
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("LoginActivity", "Error parsing error response", e)
                        }
                        
                        // Use status code specific messages if we couldn't parse
                        if (errorMessage == "Login failed. Please try again.") {
                            errorMessage = when (response.code()) {
                                401 -> "Invalid email or password"
                                400 -> "Invalid request. Please check your inputs."
                                503 -> "Database is not ready. Please try again in a moment."
                                500 -> "Server error. Please try again later."
                                else -> "Login failed. Please try again."
                            }
                        }
                        
                        Toast.makeText(
                            this@LoginActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<myapplication.test.wattwisepro.model.LoginResponse>,
                    t: Throwable
                ) {
                    loginBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                    Log.e("LoginActivity", "Login error", t)
                    Toast.makeText(
                        this@LoginActivity,
                        "Network error. Please check your connection and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun validateInputs(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                emailInput.error = "Email is required"
                emailInput.requestFocus()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInput.error = "Invalid email format"
                emailInput.requestFocus()
                return false
            }
            password.isEmpty() -> {
                passwordInput.error = "Password is required"
                passwordInput.requestFocus()
                return false
            }
        }
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
