# Database Connection Check Guide

## 🔍 How to Check if Backend is Connected to Database

### Method 1: Use Diagnostic Endpoint (Recommended)

After deploying the updated backend, visit these URLs:

1. **Health Check:**
   ```
   https://wattwisepro-production.up.railway.app/health
   ```
   
   **Expected Response (Connected):**
   ```json
   {
     "status": "healthy",
     "database": "connected",
     "testQuery": { "test": 1 },
     "timestamp": "2025-11-07T06:30:30.965Z"
   }
   ```
   
   **Expected Response (Not Connected):**
   ```json
   {
     "status": "unhealthy",
     "database": "disconnected",
     "error": "ECONNREFUSED",
     "errorCode": "ECONNREFUSED"
   }
   ```

2. **Database Diagnostic (Detailed Info):**
   ```
   https://wattwisepro-production.up.railway.app/api/db/diagnostic
   ```
   
   This endpoint shows:
   - Whether the database pool is initialized
   - Current database configuration
   - All environment variables (without sensitive data)
   - Connection test results
   - Current database name and user

### Method 2: Check Railway Dashboard

1. **Go to Railway Dashboard:**
   - Visit: https://railway.app
   - Open your project

2. **Check Backend Service:**
   - Click on your **backend service**
   - Go to **"Variables"** tab
   - Look for these environment variables:
     - ✅ `RAILWAY_PRIVATE_DOMAIN` (should have a value like `xxx.railway.internal`)
     - ✅ `MYSQLUSER` (should be set)
     - ✅ `MYSQL_ROOT_PASSWORD` (should be set)
     - ✅ `MYSQL_DATABASE` (should be set)

3. **Check Service Connection:**
   - Go to backend service → **"Settings"**
   - Under **"Connected Services"**, you should see:
     - ✅ MySQL service listed
     - ✅ Green connection indicator

4. **Check MySQL Service:**
   - Click on **MySQL service**
   - Status should be **"Active"** (green)
   - Check **"Variables"** tab to see database name

5. **Check Backend Logs:**
   - Go to backend service → **"Deployments"** → **"View Logs"**
   - Look for:
     - ✅ `✅ Database connected successfully`
     - ✅ `✅ User table ready`
   - If you see:
     - ❌ `❌ Database connection failed: ECONNREFUSED`
     - ❌ `Database config: { host: 'NOT SET', ... }`
   
   Then the database is **NOT connected**.

### Method 3: Test Login Endpoint

Try the login endpoint with test credentials:
```
POST https://wattwisepro-production.up.railway.app/api/auth/login
Content-Type: application/json

{
  "email": "john.smith@email.com",
  "password": "password123"
}
```

**If connected:**
- Returns 200 with user data OR
- Returns 401 with "Invalid email or password" (database is working)

**If NOT connected:**
- Returns 500 with "Internal server error"
- Error code: `ECONNREFUSED`

## 🔧 Common Issues and Fixes

### Issue 1: Environment Variables Not Set
**Symptoms:**
- Diagnostic shows `"NOT SET"` for environment variables
- Logs show `host: 'NOT SET'`

**Fix:**
1. Ensure MySQL service exists in Railway
2. Connect backend service to MySQL service
3. Railway will auto-inject environment variables
4. Restart backend service

### Issue 2: ECONNREFUSED Error
**Symptoms:**
- Health check returns `"database": "disconnected"`
- Error code: `ECONNREFUSED`

**Fix:**
1. Verify MySQL service is running (green status)
2. Check if services are connected
3. Try restarting both MySQL and backend services
4. Check Railway status page for outages

### Issue 3: Database Pool Not Initialized
**Symptoms:**
- Diagnostic shows `"poolInitialized": false`
- Health check returns `"database": "not_initialized"`

**Fix:**
1. Check backend logs for connection errors
2. Verify environment variables are set
3. Restart backend service
4. Wait for connection retry (happens every 5 seconds)

## 📋 Quick Checklist

- [ ] MySQL service exists in Railway
- [ ] MySQL service is running (green/active)
- [ ] Backend service is connected to MySQL service
- [ ] Environment variables are set (check Variables tab)
- [ ] Backend logs show "✅ Database connected successfully"
- [ ] Health endpoint returns `"database": "connected"`
- [ ] Diagnostic endpoint shows successful connection test

## 🚀 Next Steps

1. **Deploy the updated backend** with the new diagnostic endpoints
2. **Visit the diagnostic URL** to see current status
3. **Check Railway dashboard** for service connections
4. **Review backend logs** for connection messages
5. **Test login endpoint** to verify database access

## 📞 Still Having Issues?

If the diagnostic endpoint shows connection issues:
1. Share the diagnostic JSON response
2. Check Railway service connection status
3. Verify MySQL service is active
4. Review backend logs for detailed error messages

