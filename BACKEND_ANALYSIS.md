# Backend Analysis: Displaying Railway Database Test Data in Application

## 📊 Current State Analysis

### Backend (Node.js/Express)
**Current Backend URL:** `wattwisepro-production.up.railway.app`

#### ✅ What's Already Implemented:
1. **Database Connection** - MySQL connection pool configured for Railway
2. **User Table Creation** - Auto-creates User table if it doesn't exist
3. **Sign Up Endpoint** - `POST /api/auth/signup` (fully functional)
4. **Health Check** - `GET /health` (database connection status)
5. **Root Endpoint** - `GET /` (API status)

#### ❌ What's Missing:
1. **Login/Authentication** - No login endpoint yet
2. **Consumption Data Endpoints** - No endpoints to fetch consumption records
3. **Real-time Data** - No endpoint for live consumption
4. **Daily/Weekly/Monthly Data** - No aggregated data endpoints
5. **User-specific Data** - No endpoints that filter by user ID

---

### Android App

#### 📱 Current Activities & Their Data Needs:

1. **HomeActivity** (`home_main.xml`)
   - **Currently Shows:** Random values (hardcoded: 100-500)
   - **Needs:** 
     - Live/current consumption value (kWh)
     - Current date display
   - **Data Source:** Latest `ConsumptionRecord` from database

2. **DailyActivity** (`daily_main.xml`)
   - **Currently Shows:** Placeholder "-----/kWh" for all days
   - **Needs:** 
     - kWh values for: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
   - **Data Source:** `ConsumptionRecord` aggregated by day for current week

3. **WeeklyActivity** (`weekly_main.xml`)
   - **Currently Shows:** Placeholder "-----/kWh" for all weeks
   - **Needs:**
     - kWh values for: First Week, Second Week, Third Week, Fourth Week
   - **Data Source:** `ConsumptionRecord` aggregated by week for current month

4. **MonthlyActivity** (`monthly.xml`)
   - **Currently Shows:** Placeholder "-----/kWh" for all months
   - **Needs:**
     - kWh values for: January, February, March, April, May, June, July, August, September, October, November, December
   - **Data Source:** `UsageHistory` table or aggregated `ConsumptionRecord` by month

---

## 🗄️ Database Schema Analysis

### Existing Tables (from enhanced schema):
Based on your database schema, these tables should exist in Railway:

1. **User** ✅ (Created by backend)
   - UserID, Name, Email, Password, Address, HouseholdType, City, Subdivision, PhoneNumber

2. **SmartMeter** (Needed for consumption tracking)
   - MeterID, UserID, InstallationDate, Status, MeterType, SerialNumber

3. **ConsumptionRecord** (Core data table)
   - RecordID, MeterID, Timestamp, WattageUsed, CostEstimated, Voltage, Current, PowerFactor
   - **This is the main table for displaying consumption data**

4. **UsageHistory** (Monthly aggregations)
   - HistoryID, UserID, Month, TotalWattage, TotalCost, AverageDailyUsage, PeakUsage

---

## 📋 Required Backend Endpoints

### Priority 1: Core Data Fetching

#### 1. **Get Live/Current Consumption**
```
GET /api/consumption/live?userId={userId}
```
- Returns: Latest consumption record for user
- Response:
```json
{
  "success": true,
  "data": {
    "wattageUsed": 2500.50,
    "costEstimated": 0.30,
    "timestamp": "2024-01-01T12:00:00Z",
    "voltage": 220.5,
    "current": 11.36
  }
}
```

#### 2. **Get Daily Consumption (Weekly View)**
```
GET /api/consumption/daily?userId={userId}&startDate={YYYY-MM-DD}&endDate={YYYY-MM-DD}
```
- Returns: Daily aggregated consumption for date range
- Response:
```json
{
  "success": true,
  "data": [
    { "date": "2024-01-01", "kwh": 45.2, "dayOfWeek": "Monday" },
    { "date": "2024-01-02", "kwh": 42.8, "dayOfWeek": "Tuesday" },
    ...
  ]
}
```

#### 3. **Get Weekly Consumption (Monthly View)**
```
GET /api/consumption/weekly?userId={userId}&year={YYYY}&month={MM}
```
- Returns: Weekly aggregated consumption for a month
- Response:
```json
{
  "success": true,
  "data": [
    { "weekNumber": 1, "kwh": 320.5, "startDate": "2024-01-01", "endDate": "2024-01-07" },
    { "weekNumber": 2, "kwh": 298.3, "startDate": "2024-01-08", "endDate": "2024-01-14" },
    ...
  ]
}
```

#### 4. **Get Monthly Consumption (Yearly View)**
```
GET /api/consumption/monthly?userId={userId}&year={YYYY}
```
- Returns: Monthly aggregated consumption for a year
- Response:
```json
{
  "success": true,
  "data": [
    { "month": 1, "monthName": "January", "kwh": 1250.50, "totalCost": 150.06 },
    { "month": 2, "monthName": "February", "kwh": 1180.25, "totalCost": 141.63 },
    ...
  ]
}
```

### Priority 2: Authentication

#### 5. **Login Endpoint**
```
POST /api/auth/login
```
- Request: `{ "email": "...", "password": "..." }`
- Returns: User data + authentication token
- Response:
```json
{
  "success": true,
  "message": "Login successful",
  "user": {
    "userId": 1,
    "name": "John Smith",
    "email": "john@example.com",
    ...
  },
  "token": "jwt_token_here"
}
```

---

## 🔧 Implementation Plan

### Phase 1: Database Verification
1. ✅ Verify tables exist in Railway database
2. ✅ Check if test data exists in `ConsumptionRecord` table
3. ✅ Verify table structure matches expected schema

### Phase 2: Backend Endpoints
1. Create `/api/consumption/live` endpoint
2. Create `/api/consumption/daily` endpoint
3. Create `/api/consumption/weekly` endpoint
4. Create `/api/consumption/monthly` endpoint
5. Add authentication middleware (optional but recommended)

### Phase 3: Android Integration
1. Update `ApiService.kt` with new endpoints
2. Create response models for consumption data
3. Update `HomeActivity.kt` to fetch live data
4. Update `DailyActivity.kt` to fetch daily data
5. Update `WeeklyActivity.kt` to fetch weekly data
6. Update `MonthlyActivity.kt` to fetch monthly data
7. Update `RetrofitClient.kt` with correct Railway URL

### Phase 4: Error Handling & Loading States
1. Add loading indicators in all activities
2. Add error handling for network failures
3. Add empty state handling (no data)
4. Cache data for offline viewing (optional)

---

## 📝 SQL Queries Needed

### For Live Consumption:
```sql
SELECT WattageUsed, CostEstimated, Timestamp, Voltage, Current
FROM ConsumptionRecord cr
JOIN SmartMeter sm ON cr.MeterID = sm.MeterID
WHERE sm.UserID = ?
ORDER BY cr.Timestamp DESC
LIMIT 1;
```

### For Daily Consumption (Week):
```sql
SELECT 
    DATE(cr.Timestamp) as date,
    DAYNAME(cr.Timestamp) as dayOfWeek,
    SUM(cr.WattageUsed) / 1000 as kwh
FROM ConsumptionRecord cr
JOIN SmartMeter sm ON cr.MeterID = sm.MeterID
WHERE sm.UserID = ?
AND cr.Timestamp BETWEEN ? AND ?
GROUP BY DATE(cr.Timestamp)
ORDER BY date;
```

### For Weekly Consumption (Month):
```sql
SELECT 
    WEEK(cr.Timestamp) - WEEK(DATE_FORMAT(cr.Timestamp, '%Y-%m-01')) + 1 as weekNumber,
    SUM(cr.WattageUsed) / 1000 as kwh,
    MIN(cr.Timestamp) as startDate,
    MAX(cr.Timestamp) as endDate
FROM ConsumptionRecord cr
JOIN SmartMeter sm ON cr.MeterID = sm.MeterID
WHERE sm.UserID = ?
AND YEAR(cr.Timestamp) = ?
AND MONTH(cr.Timestamp) = ?
GROUP BY weekNumber
ORDER BY weekNumber;
```

### For Monthly Consumption (Year):
```sql
SELECT 
    MONTH(cr.Timestamp) as month,
    MONTHNAME(cr.Timestamp) as monthName,
    SUM(cr.WattageUsed) / 1000 as kwh,
    SUM(cr.CostEstimated) as totalCost
FROM ConsumptionRecord cr
JOIN SmartMeter sm ON cr.MeterID = sm.MeterID
WHERE sm.UserID = ?
AND YEAR(cr.Timestamp) = ?
GROUP BY MONTH(cr.Timestamp)
ORDER BY month;
```

---

## ⚠️ Current Issues to Address

1. **No User Authentication** - App needs to identify which user's data to show
2. **Hardcoded Random Values** - HomeActivity uses random numbers instead of real data
3. **No API Integration** - Activities don't make API calls yet
4. **Missing User ID** - Need to pass userId from login/signup to activities
5. **Backend URL Not Set** - `RetrofitClient.kt` still has placeholder URL

---

## 🎯 Next Steps (After Analysis)

1. **Verify Railway Database** - Check what tables and test data exist
2. **Create Backend Endpoints** - Implement consumption data endpoints
3. **Update Android Activities** - Integrate API calls with UI
4. **Test Data Flow** - Verify end-to-end data flow from database to app
5. **Add Loading States** - Improve UX with loading indicators
6. **Error Handling** - Handle network errors gracefully

---

## 📍 Railway Database Connection Info

- **URL:** `wattwisepro-production.up.railway.app`
- **Database:** Should be in Railway environment variables
- **Tables Needed:**
  - User (✅ exists)
  - SmartMeter (❓ need to verify)
  - ConsumptionRecord (❓ need to verify)
  - UsageHistory (❓ optional, can calculate from ConsumptionRecord)

---

## 🔍 Questions to Answer

1. **Does Railway database have test data?**
   - Need to check if `ConsumptionRecord` has sample data
   - If not, need to insert test data

2. **Do all required tables exist?**
   - Verify SmartMeter, ConsumptionRecord tables exist
   - Check table structure matches schema

3. **User Authentication Flow:**
   - How will user ID be passed to activities?
   - Should we implement login first?
   - Or use a simple userId parameter for now?

4. **Data Aggregation:**
   - Calculate on-the-fly from ConsumptionRecord?
   - Or use pre-aggregated UsageHistory table?

---

## 📌 Summary

**Current Status:**
- ✅ Backend is deployed and connected to Railway MySQL
- ✅ Sign up functionality works
- ❌ No consumption data endpoints yet
- ❌ App uses hardcoded/mock data
- ❌ No real-time data integration

**What Needs to Be Done:**
1. Verify database tables and test data exist
2. Create 4-5 consumption data endpoints
3. Update Android app to fetch and display real data
4. Implement user authentication (or simple userId passing)
5. Test end-to-end data flow

