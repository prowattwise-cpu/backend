# Android App Setup Guide

This guide will help you configure the Android app to connect to the backend API.

## 📋 Prerequisites

- Android Studio installed
- Project opened in Android Studio
- Backend URL from Railway (will be provided after deployment)

---

## 🔧 Step 1: Find and Update RetrofitClient.kt

### Locate the File
1. In Android Studio, open the **Project** view (if not already open)
2. Navigate to: `app/src/main/java/myapplication/test/wattwisepro/api/RetrofitClient.kt`
   - If the `api` folder doesn't exist, create it in the same directory structure
   - If `RetrofitClient.kt` doesn't exist, create it (see template below)

### Update BASE_URL

Open `RetrofitClient.kt` and find the `BASE_URL` constant:

```kotlin
private const val BASE_URL = "YOUR_BACKEND_URL_HERE"
```

**Replace it with one of these options:**

#### Option A: Using Railway (Production - Recommended)
After backend is deployed to Railway, you'll receive a URL like:
```kotlin
private const val BASE_URL = "https://your-backend-app.railway.app/"
```
⚠️ **Important:** Make sure to include the trailing slash (`/`)!

#### Option B: Using Local Backend (Development Testing)

**For Android Emulator:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/"
```

**For Physical Device:**
You'll need the computer's IP address where the backend is running:
```kotlin
private const val BASE_URL = "http://192.168.100.20:3000/"
// OR
private const val BASE_URL = "http://192.168.56.1:3000/"
```
(Use the IP address provided by the backend developer)

---

## 📝 RetrofitClient.kt Template

If the file doesn't exist, create it with this template:

```kotlin
package myapplication.test.wattwisepro.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // UPDATE THIS URL WITH YOUR BACKEND URL
    private const val BASE_URL = "https://your-backend-app.railway.app/"
    
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
```

---

## 🔄 Step 2: Sync Gradle

After updating the URL:

1. In Android Studio, go to **File → Sync Project with Gradle Files**
2. Wait for the sync to complete
3. Check for any errors in the **Build** tab

---

## ✅ Step 3: Verify the Setup

### Check File Structure
Make sure your project structure looks like this:
```
app/
└── src/
    └── main/
        └── java/
            └── myapplication/
                └── test/
                    └── wattwisepro/
                        └── api/
                            └── RetrofitClient.kt
```

### Verify API Service Interface
Ensure you have an `ApiService` interface that matches the backend endpoints:

```kotlin
package myapplication.test.wattwisepro.api

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/signup")
    fun signUp(@Body user: SignUpRequest): Call<SignUpResponse>
    
    // Add other endpoints as needed
}
```

---

## 🧪 Step 4: Test the Connection

### Test Checklist:
- [ ] RetrofitClient.kt file exists and has correct BASE_URL
- [ ] Gradle sync completed without errors
- [ ] App builds successfully
- [ ] Backend is running (check with backend developer)
- [ ] Network security config allows HTTP (if using HTTP, not HTTPS)

### Network Security Config (if using HTTP)

If using HTTP (like `http://10.0.2.2:3000/`), you need to allow cleartext traffic:

1. Create `app/src/main/res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">192.168.100.20</domain>
        <domain includeSubdomains="true">192.168.56.1</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

2. Add to `AndroidManifest.xml` inside `<application>` tag:
```xml
<application
    ...
    android:usesCleartextTraffic="true"
    android:networkSecurityConfig="@xml/network_security_config">
    ...
</application>
```

---

## 🐛 Troubleshooting

### Error: "Unable to resolve host"
- ✅ Check BASE_URL is correct
- ✅ Check backend is running
- ✅ For emulator, use `10.0.2.2` instead of `localhost`
- ✅ For physical device, verify IP address is correct
- ✅ Check internet connection

### Error: "Connection refused"
- ✅ Backend might not be running
- ✅ Check if backend port (3000) matches the URL
- ✅ Verify firewall isn't blocking the connection

### Error: "CLEARTEXT communication not permitted"
- ✅ Add network security config (see Step 4 above)
- ✅ Or use HTTPS (Railway deployment provides HTTPS)

### Build Errors After URL Change
- ✅ Clean and rebuild: **Build → Clean Project**, then **Build → Rebuild Project**
- ✅ Invalidate caches: **File → Invalidate Caches → Invalidate and Restart**

---

## 📱 Testing the Sign Up Feature

Once everything is configured:

1. **Run the app** on emulator or device
2. **Navigate to Sign Up screen**
3. **Fill in all fields:**
   - Name
   - Email
   - Password
   - Repeat Password
   - Address
   - Household Type (select from spinner)
   - City (should default to "Tarlac City")
   - Subdivision
   - Phone Number
4. **Click "Create Account"**
5. **Check the response:**
   - Success: Should navigate to Login screen
   - Error: Check error message and verify backend connection

---

## 🔗 Backend URLs Reference

| Environment | URL Format | Example |
|-------------|-----------|---------|
| Railway (Production) | `https://your-app.railway.app/` | `https://wattwisepro.railway.app/` |
| Local (Emulator) | `http://10.0.2.2:3000/` | - |
| Local (Physical Device) | `http://IP_ADDRESS:3000/` | `http://192.168.100.20:3000/` |

**Important:** Always include the trailing slash (`/`) in BASE_URL!

---

## 📞 Need Help?

If you encounter issues:
1. Check the backend is running and accessible
2. Verify the BASE_URL format is correct
3. Check Android Studio's Build/Lint tabs for errors
4. Test the backend URL in a browser first (add `/health` to the URL)
5. Contact the backend developer for the correct URL

---

## ✅ Final Checklist

Before submitting/testing:
- [ ] BASE_URL is updated correctly
- [ ] Gradle sync completed
- [ ] App builds without errors
- [ ] Network security config added (if using HTTP)
- [ ] Tested sign up functionality
- [ ] Success/error messages display correctly
- [ ] Navigation works after sign up

