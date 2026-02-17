package myapplication.test.wattwisepro.utils

import android.content.Context
import android.content.SharedPreferences
import myapplication.test.wattwisepro.model.UserData

object SessionManager {
    private const val PREF_NAME = "WattWiseProSession"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    
    private lateinit var sharedPreferences: SharedPreferences
    
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveSession(user: UserData) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, user.userId)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }
    
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }
    
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }
    
    fun logout() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}

