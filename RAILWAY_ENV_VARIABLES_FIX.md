# Railway Environment Variables Fix Guide

## 🔴 Problem: Missing Environment Variables

Your backend logs show:
```
- MYSQL_ROOT_PASSWORD: NOT SET
- MYSQL_DATABASE: NOT SET
```

This means Railway is **not automatically injecting** the MySQL environment variables into your backend service.

## ✅ Solution: Connect MySQL Service to Backend Service

### Step 1: Verify MySQL Service Exists

1. Go to Railway Dashboard: https://railway.app
2. Open your project
3. Check if you have a **MySQL** service
   - If NO: Add one (see Step 2)
   - If YES: Go to Step 3

### Step 2: Add MySQL Service (if missing)

1. In Railway project, click **"+ New"**
2. Select **"Database"** → **"Add MySQL"**
3. Wait for MySQL service to provision (1-2 minutes)
4. Railway will create a MySQL database instance

### Step 3: Connect MySQL to Backend Service

**This is the critical step!** Railway needs to know that your backend service should use the MySQL service.

#### Method 1: Via Service Settings (Recommended)

1. Go to your **Backend Service** (not MySQL service)
2. Click **"Settings"** tab
3. Scroll down to **"Connected Services"** or **"Networking"** section
4. Click **"+ Add Connection"** or **"Connect"**
5. Select your **MySQL service** from the list
6. Click **"Connect"** or **"Save"**

#### Method 2: Via MySQL Service

1. Go to your **MySQL Service**
2. Click **"Settings"** tab
3. Look for **"Connected Services"** or **"Networking"**
4. Click **"+ Add Connection"**
5. Select your **Backend Service**
6. Click **"Connect"**

### Step 4: Verify Environment Variables

After connecting the services:

1. Go to **Backend Service** → **"Variables"** tab
2. You should now see these variables **automatically added**:
   - ✅ `MYSQLUSER` (e.g., `root`)
   - ✅ `MYSQL_ROOT_PASSWORD` (e.g., `abc123xyz...`)
   - ✅ `MYSQL_DATABASE` (e.g., `railway` or `mysql`)
   - ✅ `RAILWAY_PRIVATE_DOMAIN` (e.g., `xxx.railway.internal`)

3. **If variables are still missing:**
   - Disconnect and reconnect the services
   - Restart the backend service
   - Wait a few minutes for Railway to sync

### Step 5: Set Database Name (if needed)

Railway might set `MYSQL_DATABASE` to `railway` or `mysql` by default.

**If you need a specific database name:**

1. Go to **Backend Service** → **"Variables"** tab
2. Look for `MYSQL_DATABASE`
3. If it's not `smart_energy_tracking`, you can:
   - **Option A:** Change it to `smart_energy_tracking` (if database exists)
   - **Option B:** Keep the default and update your code to use that name
   - **Option C:** Create the database manually via MySQL service

**To create database manually:**
1. Go to **MySQL Service** → **"Connect"** tab
2. Use Railway's database proxy or connect via MySQL client
3. Run: `CREATE DATABASE IF NOT EXISTS smart_energy_tracking;`
4. Set `MYSQL_DATABASE=smart_energy_tracking` in backend variables

### Step 6: Restart Backend Service

1. Go to **Backend Service** → **"Settings"**
2. Click **"Restart"**
3. Wait for service to restart
4. Check **"Deployments"** → **"View Logs"**

### Step 7: Verify Connection

Check backend logs for:
```
✅ Database connected successfully
✅ User table ready
```

**If you still see errors:**
- Check that all environment variables are set
- Verify services are connected
- Check Railway status page for outages

## 🔍 Troubleshooting

### Issue: Variables still not showing after connecting

**Solution:**
1. **Disconnect and reconnect:**
   - Backend Service → Settings → Connected Services
   - Disconnect MySQL service
   - Wait 30 seconds
   - Reconnect MySQL service

2. **Restart both services:**
   - Restart MySQL service
   - Restart Backend service

3. **Check service names:**
   - Ensure service names are correct
   - No special characters that might cause issues

### Issue: MYSQL_DATABASE is set but wrong name

**Solution:**
1. Check what database name Railway created
2. Either:
   - Update backend code to use Railway's database name
   - Or create your own database and set `MYSQL_DATABASE` variable

### Issue: Connection works but data not saving

**Possible causes:**
1. **Table name case sensitivity:**
   - MySQL on Linux is case-sensitive
   - Table name is `User` (capital U)
   - Make sure queries use exact case

2. **Database name mismatch:**
   - Backend connects to one database
   - You're checking a different database
   - Verify `MYSQL_DATABASE` value

3. **Transaction not committed:**
   - Check if queries are executing
   - Look for errors in backend logs

## 📋 Quick Checklist

- [ ] MySQL service exists in Railway
- [ ] MySQL service is running (green/active)
- [ ] Backend service is **connected** to MySQL service
- [ ] Environment variables are set in backend service:
  - [ ] `MYSQLUSER`
  - [ ] `MYSQL_ROOT_PASSWORD`
  - [ ] `MYSQL_DATABASE`
  - [ ] `RAILWAY_PRIVATE_DOMAIN`
- [ ] Backend service has been restarted
- [ ] Backend logs show "✅ Database connected successfully"
- [ ] Health endpoint returns `"database": "connected"`

## 🆘 Still Not Working?

### Check Railway Documentation
- Railway MySQL Service: https://docs.railway.app/databases/mysql
- Environment Variables: https://docs.railway.app/develop/variables

### Manual Variable Setup (Last Resort)
If automatic injection doesn't work:

1. Go to **MySQL Service** → **"Variables"** tab
2. Copy these values:
   - `MYSQLUSER`
   - `MYSQL_ROOT_PASSWORD`
   - `MYSQL_DATABASE`
   - `RAILWAY_PRIVATE_DOMAIN`

3. Go to **Backend Service** → **"Variables"** tab
4. Click **"+ New Variable"**
5. Manually add each variable with copied values

**⚠️ Warning:** Manual setup means variables won't auto-update if MySQL service changes.

## 📞 Next Steps

1. **Connect services** in Railway dashboard
2. **Verify variables** are auto-injected
3. **Restart backend** service
4. **Check logs** for successful connection
5. **Test signup/login** in your Android app

