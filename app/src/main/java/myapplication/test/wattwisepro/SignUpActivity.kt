package myapplication.test.wattwisepro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import myapplication.test.wattwisepro.api.RetrofitClient
import myapplication.test.wattwisepro.model.SignUpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var repeatPasswordInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var householdTypeSpinner: Spinner
    private lateinit var cityInput: EditText
    private lateinit var subdivisionInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var createBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar

    // Household types are now defined in strings.xml as household_types array

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("SignUpActivity", "onCreate() called")
        initializeViews()
        setupSpinner()
        setupClickListeners()
        Log.d("SignUpActivity", "onCreate() completed")
    }

    private fun initializeViews() {
        Log.d("SignUpActivity", "Initializing views...")
        
        usernameInput = findViewById(R.id.email_input)
        emailInput = findViewById(R.id.email_address_input)
        passwordInput = findViewById(R.id.password_input)
        repeatPasswordInput = findViewById(R.id.repeat_password_input)
        addressInput = findViewById(R.id.address_input)
        householdTypeSpinner = findViewById(R.id.household_type_spinner)
        cityInput = findViewById(R.id.city_input)
        subdivisionInput = findViewById(R.id.subdivision_input)
        phoneNumberInput = findViewById(R.id.phone_number_input)
        createBtn = findViewById(R.id.create_btn)
        loginBtn = findViewById(R.id.tvLogin)
        
        Log.d("SignUpActivity", "Views initialized:")
        Log.d("SignUpActivity", "  usernameInput: ${usernameInput != null}")
        Log.d("SignUpActivity", "  emailInput: ${emailInput != null}")
        Log.d("SignUpActivity", "  passwordInput: ${passwordInput != null}")
        Log.d("SignUpActivity", "  householdTypeSpinner: ${householdTypeSpinner != null}")
        Log.d("SignUpActivity", "  createBtn: ${createBtn != null}")
        
        // Create progress bar programmatically if not in layout
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }
        
        Log.d("SignUpActivity", "Views initialization completed")
    }

    private fun setupSpinner() {
        Log.d("SignUpActivity", "Setting up spinner...")
        
        try {
            // Create adapter from string array resource
            Log.d("SignUpActivity", "Creating adapter from R.array.household_types")
            val adapter = ArrayAdapter.createFromResource(
                this,
                R.array.household_types,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            
            Log.d("SignUpActivity", "Adapter created with ${adapter.count} items")
            
            // Set adapter to spinner
            householdTypeSpinner.adapter = adapter
            Log.d("SignUpActivity", "Adapter set to spinner")
            
            // Set default selection to "Apartment" (index 0)
            householdTypeSpinner.setSelection(0, false)
            Log.d("SignUpActivity", "Default selection set to index 0")
            
            // Ensure spinner is clickable and focusable
            householdTypeSpinner.isClickable = true
            householdTypeSpinner.isFocusable = true
            householdTypeSpinner.isFocusableInTouchMode = true
            householdTypeSpinner.isEnabled = true
            Log.d("SignUpActivity", "Spinner properties set: clickable=true, focusable=true, enabled=true")
            
            // Add item selected listener for debugging
            householdTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent?.getItemAtPosition(position) as? String
                    Log.d("SignUpActivity", "Spinner item selected: $selectedItem at position $position")
                }
                
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("SignUpActivity", "Spinner: nothing selected")
                }
            }
            
            // Ensure parent ScrollView doesn't intercept touch events
            householdTypeSpinner.setOnTouchListener { view, event ->
                Log.d("SignUpActivity", "Spinner touch event: action=${event.action}")
                // Prevent parent ScrollView from intercepting touch events
                view.parent?.requestDisallowInterceptTouchEvent(true)
                // Return false to let the spinner handle the event normally
                false
            }
            
            // Log for debugging
            Log.d("SignUpActivity", "Spinner initialized with ${adapter.count} items")
            Log.d("SignUpActivity", "Spinner isEnabled: ${householdTypeSpinner.isEnabled}")
            Log.d("SignUpActivity", "Spinner isClickable: ${householdTypeSpinner.isClickable}")
            Log.d("SignUpActivity", "Spinner isFocusable: ${householdTypeSpinner.isFocusable}")
            Log.d("SignUpActivity", "Spinner adapter count: ${adapter.count}")
            Log.d("SignUpActivity", "Spinner selected item: ${householdTypeSpinner.selectedItem}")
        } catch (e: Exception) {
            Log.e("SignUpActivity", "Error setting up spinner", e)
            e.printStackTrace()
            // Fallback to hardcoded array if resource fails
            val fallbackAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                arrayOf("Apartment", "Family", "House", "Single")
            )
            fallbackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            householdTypeSpinner.adapter = fallbackAdapter
            householdTypeSpinner.setSelection(0)
            
            // Ensure spinner is clickable even with fallback
            householdTypeSpinner.isClickable = true
            householdTypeSpinner.isFocusable = true
            householdTypeSpinner.isFocusableInTouchMode = true
            householdTypeSpinner.isEnabled = true
        }
    }

    private fun setupClickListeners() {
        Log.d("SignUpActivity", "Setting up click listeners...")

        loginBtn.setOnClickListener {
            Log.d("SignUpActivity", "Login button clicked")
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(0, 0)
        }

        createBtn.setOnClickListener {
            Log.d("SignUpActivity", "🔴 Create Account button clicked!")
            handleSignUp()
        }
        
        Log.d("SignUpActivity", "Click listeners set up")
    }

    private fun handleSignUp() {
        Log.d("SignUpActivity", "🔵 handleSignUp() called")
        
        val name = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val repeatPassword = repeatPasswordInput.text.toString()
        val address = addressInput.text.toString().trim()
        val householdType = householdTypeSpinner.selectedItem.toString()
        val city = cityInput.text.toString().trim()
        val subdivision = subdivisionInput.text.toString().trim()
        val phoneNumber = phoneNumberInput.text.toString().trim()

        // Log input values for debugging
        Log.d("SignUpActivity", "📝 Signup attempt:")
        Log.d("SignUpActivity", "  Name: '$name' (length: ${name.length})")
        Log.d("SignUpActivity", "  Email: '$email'")
        Log.d("SignUpActivity", "  Password: '***' (length: ${password.length})")
        Log.d("SignUpActivity", "  Repeat Password: '***' (length: ${repeatPassword.length})")
        Log.d("SignUpActivity", "  Address: '$address' (length: ${address.length})")
        Log.d("SignUpActivity", "  Household Type: '$householdType'")
        Log.d("SignUpActivity", "  City: '$city' (length: ${city.length})")
        Log.d("SignUpActivity", "  Subdivision: '$subdivision' (length: ${subdivision.length})")
        Log.d("SignUpActivity", "  Phone Number: '$phoneNumber' (length: ${phoneNumber.length})")

        // Validate inputs
        Log.d("SignUpActivity", "🔍 Validating inputs...")
        if (!validateInputs(name, email, password, repeatPassword, address, city, subdivision, phoneNumber)) {
            Log.e("SignUpActivity", "❌ Validation failed - not sending request")
            return
        }

        Log.d("SignUpActivity", "✅ Validation passed - sending signup request")

        // Show loading
        createBtn.isEnabled = false
        progressBar.visibility = View.VISIBLE

        // Create signup request
        val signUpRequest = SignUpRequest(
            name = name,
            email = email,
            password = password,
            address = address,
            householdType = householdType,
            city = city,
            subdivision = subdivision,
            phoneNumber = phoneNumber
        )

        Log.d("SignUpActivity", "📦 Created SignUpRequest object")
        Log.d("SignUpActivity", "🌐 Making API call to signup endpoint...")

        // Make API call
        RetrofitClient.apiService.signUp(signUpRequest)
            .enqueue(object : Callback<myapplication.test.wattwisepro.model.SignUpResponse> {
                override fun onResponse(
                    call: Call<myapplication.test.wattwisepro.model.SignUpResponse>,
                    response: Response<myapplication.test.wattwisepro.model.SignUpResponse>
                ) {
                    createBtn.isEnabled = true
                    progressBar.visibility = View.GONE

                    Log.d("SignUpActivity", "Signup response received")
                    Log.d("SignUpActivity", "Response code: ${response.code()}")
                    Log.d("SignUpActivity", "Response isSuccessful: ${response.isSuccessful}")

                    if (response.isSuccessful && response.body() != null) {
                        val signUpResponse = response.body()!!
                        Log.d("SignUpActivity", "Signup response body: success=${signUpResponse.success}, message=${signUpResponse.message}, userId=${signUpResponse.userId}")
                        
                        if (signUpResponse.success) {
                            Log.d("SignUpActivity", "✅ Signup successful! UserID: ${signUpResponse.userId}")
                            Toast.makeText(
                                this@SignUpActivity,
                                "Account created successfully! Please log in.",
                                Toast.LENGTH_LONG
                            ).show()
                            // Navigate to login
                            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Log.e("SignUpActivity", "❌ Signup failed: ${signUpResponse.message}")
                            Toast.makeText(
                                this@SignUpActivity,
                                signUpResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Try to parse error body
                        val errorBody = response.errorBody()?.string()
                        Log.e("SignUpActivity", "❌ Signup failed - Response not successful")
                        Log.e("SignUpActivity", "Error body: $errorBody")
                        Log.e("SignUpActivity", "Response code: ${response.code()}")
                        
                        Toast.makeText(
                            this@SignUpActivity,
                            "Sign up failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<myapplication.test.wattwisepro.model.SignUpResponse>,
                    t: Throwable
                ) {
                    createBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                    Log.e("SignUpActivity", "❌ Signup network error", t)
                    Log.e("SignUpActivity", "Error message: ${t.message}")
                    Log.e("SignUpActivity", "Error cause: ${t.cause}")
                    Toast.makeText(
                        this@SignUpActivity,
                        "Network error. Please check your connection and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        repeatPassword: String,
        address: String,
        city: String,
        subdivision: String,
        phoneNumber: String
    ): Boolean {
        when {
            name.isEmpty() -> {
                usernameInput.error = "Username is required"
                usernameInput.requestFocus()
                return false
            }
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
            password.length < 6 -> {
                passwordInput.error = "Password must be at least 6 characters"
                passwordInput.requestFocus()
                return false
            }
            repeatPassword.isEmpty() -> {
                repeatPasswordInput.error = "Please repeat your password"
                repeatPasswordInput.requestFocus()
                return false
            }
            password != repeatPassword -> {
                // Show pop-up alert dialog
                AlertDialog.Builder(this)
                    .setTitle("Password Mismatch")
                    .setMessage("Password must be the same.")
                    .setPositiveButton("OK") { dialog, _ -> 
                        dialog.dismiss()
                        repeatPasswordInput.requestFocus()
                    }
                    .setCancelable(false)
                    .show()
                return false
            }
            address.isEmpty() -> {
                addressInput.error = "Address is required"
                addressInput.requestFocus()
                return false
            }
            city.isEmpty() -> {
                cityInput.error = "City is required"
                cityInput.requestFocus()
                return false
            }
            subdivision.isEmpty() -> {
                subdivisionInput.error = "Subdivision is required"
                subdivisionInput.requestFocus()
                return false
            }
            phoneNumber.isEmpty() -> {
                phoneNumberInput.error = "Phone number is required"
                phoneNumberInput.requestFocus()
                return false
            }
            phoneNumber.length < 10 -> {
                phoneNumberInput.error = "Invalid phone number"
                phoneNumberInput.requestFocus()
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
