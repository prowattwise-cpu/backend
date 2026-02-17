# Railway Database Connection Fix Guide

## 🔴 Problem: ECONNREFUSED Error

Your backend is getting `ECONNREFUSED` when trying to connect to MySQL. This means:
- The MySQL service is either not running, not connected, or not accessible
- The connection is being refused at the network level

## ✅ Solution Steps

### Step 1: Verify MySQL Service Exists
1. Go to your Railway dashboard: https://railway.app
2. Open your project
3. Check if you have a **MySQL** service
   - If NO: You need to add one (see Step 2)
   - If YES: Go to Step 3

### Step 2: Add MySQL Service (if missing)
1. In your Railway project, click **"+ New"**
2. Select **"Database"** → **"Add MySQL"**
3. Railway will create a MySQL database instance
4. Wait for it to fully provision (may take 1-2 minutes)

### Step 3: Link MySQL to Backend Service
1. Go to your **backend service** (not MySQL service)
2. Click on **"Variables"** tab
3. Look for these environment variables (should be auto-injected):
   - `MYSQLUSER`
   - `MYSQL_ROOT_PASSWORD`
   - `MYSQL_DATABASE`
   - `RAILWAY_PRIVATE_DOMAIN`
   
   **If these are missing**, you need to connect the services:
   1. Go to backend service settings
   2. Find **"Connect"** or **"Networking"** section
   3. Select the MySQL service to connect
   4. Railway will automatically inject the environment variables

### Step 4: Verify Service Connection
In Railway:
1. Go to your **backend service**
2. Click **"Settings"**
3. Under **"Connected Services"**, you should see:
   - ✅ MySQL service listed
   - ✅ Green connection indicator

### Step 5: Check Environment Variables
1. Go to **backend service** → **"Variables"** tab
2. Verify these exist and have values:
   ```
   RAILWAY_PRIVATE_DOMAIN = wattwisepro.railway.internal (or similar)
   MYSQLUSER = root (or your MySQL user)
   MYSQL_ROOT_PASSWORD = [should be set]
   MYSQL_DATABASE = smart_energy_tracking (or your database name)
   MYSQL_PORT = 3306
   ```

### Step 6: Restart Services
1. Go to **backend service**
2. Click **"Settings"** → **"Restart"**
3. Wait for service to restart
4. Check logs - you should see:
   ```
   ✅ Database connected successfully
   ✅ User table ready
   ```

### Step 7: Test Connection
1. Go to backend service logs
2. You should see connection attempts
3. If successful: `✅ Database connected successfully`
4. If still failing: Check the error messages

## 🔍 Troubleshooting

### Issue: MySQL service exists but not connected
**Solution:**
1. In backend service → Settings → Connected Services
2. Click **"Connect"** or **"+ Add Connection"**
3. Select your MySQL service
4. Railway will auto-inject environment variables

### Issue: Environment variables are empty
**Solution:**
1. Verify MySQL service is running (green status)
2. Ensure services are connected (Step 3)
3. Try disconnecting and reconnecting the MySQL service

### Issue: MySQL service is not running
**Solution:**
1. Check MySQL service status
2. If stopped, click **"Start"**
3. Wait for it to fully start (green indicator)

### Issue: Wrong database name
**Solution:**
1. Check `MYSQL_DATABASE` variable
2. It should match your actual database name
3. Default might be `railway` or `mysql`
4. Update it to `smart_energy_tracking` if needed

## 📋 Quick Checklist

- [ ] MySQL service exists in Railway
- [ ] MySQL service is running (green status)
- [ ] Backend service is connected to MySQL service
- [ ] Environment variables are set:
  - [ ] `RAILWAY_PRIVATE_DOMAIN`
  - [ ] `MYSQLUSER`
  - [ ] `MYSQL_ROOT_PASSWORD`
  - [ ] `MYSQL_DATABASE`
- [ ] Backend service has been restarted
- [ ] Logs show successful connection

## 🆘 Still Not Working?

### Manual Connection Test
Try connecting directly to verify credentials:
1. Use Railway's database proxy
2. Or check MySQL service → **"Connect"** → **"Variables"** to see all connection details

### Check Railway Status
1. Go to Railway status page
2. Check if there are any outages
3. MySQL service might be experiencing issues

### Alternative: Use Public Connection
If internal connection doesn't work, you can use:
- `RAILWAY_TCP_PROXY_DOMAIN` instead of `RAILWAY_PRIVATE_DOMAIN`
- `RAILWAY_TCP_PROXY_PORT` instead of `3306`

Update the dbConfig in server.js to try both:
```javascript
host: process.env.RAILWAY_TCP_PROXY_DOMAIN || process.env.RAILWAY_PRIVATE_DOMAIN || process.env.MYSQL_HOST,
port: process.env.RAILWAY_TCP_PROXY_PORT || process.env.MYSQL_PORT || 3306,
```

