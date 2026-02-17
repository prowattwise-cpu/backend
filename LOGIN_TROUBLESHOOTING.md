# Login Troubleshooting Guide

## 🔴 Problem: "Invalid email or password" even though credentials are in database

### Possible Causes:

1. **Password not hashed with bcrypt** (Most Common)
   - User was created manually in database
   - Password is stored as plain text or different format
   - Backend expects bcrypt hashed passwords

2. **Email case mismatch**
   - Email in database has different casing
   - MySQL might be case-sensitive depending on collation

3. **Wrong table name**
   - Data is in `user` table but code checks `User` table (or vice versa)
   - Case sensitivity issues

4. **Password doesn't match**
   - Password in database is different from what you're entering
   - Password was changed manually

## ✅ Solutions:

### Solution 1: Create User Through Signup Endpoint (Recommended)

**This is the best solution** - Create a new user through the signup page:

1. Go to signup page in your Android app
2. Fill in all fields:
   - Name
   - Email: `john.smith@email.com` (or use a different email)
   - Password: `password123`
   - Repeat Password: `password123`
   - Address
   - Household Type: Select from dropdown
   - City
   - Subdivision
   - Phone Number
3. Click "Create Account"
4. Try logging in with the same credentials

**Why this works:**
- Signup endpoint hashes password with bcrypt
- User is created in the correct table
- All fields are properly formatted

### Solution 2: Re-hash Existing User's Password

If you need to keep the existing user, you need to re-hash the password:

1. **Option A: Use Node.js script**
   ```javascript
   const bcrypt = require('bcryptjs');
   const password = 'password123';
   const hashed = await bcrypt.hash(password, 10);
   console.log(hashed);
   // Update database with this hashed password
   ```

2. **Option B: Update via MySQL**
   ```sql
   -- First, hash the password using bcrypt
   -- Then update:
   UPDATE User SET Password = '$2a$10$...hashed_password...' WHERE Email = 'john.smith@email.com';
   ```

### Solution 3: Check Railway Backend Logs

The backend now has detailed logging. Check Railway logs to see:

1. **Go to Railway Dashboard** → Your Backend Service → Deployments → View Logs
2. **Look for these messages:**
   - `🔍 Login attempt for email: john.smith@email.com`
   - `📊 Found X user(s) with this email`
   - `✅ User found: [name] (ID: [id])`
   - `🔐 Password is hashed: true/false`
   - `🔐 Password verification: VALID/INVALID`

3. **Common log messages:**
   - `❌ No user found with email` → User doesn't exist
   - `⚠️ WARNING: Password is not hashed with bcrypt!` → Password needs re-hashing
   - `❌ Password mismatch` → Password doesn't match

### Solution 4: Verify User in Database

1. **Connect to Railway MySQL:**
   - Go to Railway → MySQL Service → Connect
   - Use Railway's database proxy or MySQL client

2. **Check if user exists:**
   ```sql
   SELECT UserID, Name, Email, LEFT(Password, 20) as PasswordHash FROM User WHERE Email = 'john.smith@email.com';
   ```

3. **Check password format:**
   - Bcrypt hashes start with: `$2a$`, `$2b$`, or `$2y$`
   - If password doesn't start with these, it's not hashed correctly

4. **Check table name:**
   ```sql
   -- Check both tables
   SELECT * FROM User WHERE Email = 'john.smith@email.com';
   SELECT * FROM user WHERE Email = 'john.smith@email.com';
   ```

## 🔍 Debugging Steps:

### Step 1: Check Backend Logs
- Look for detailed login attempt logs
- Check which table was used
- Check if password is hashed

### Step 2: Verify Database
- Check if user exists
- Check password format
- Check email casing

### Step 3: Test with New User
- Create new user through signup
- Try logging in with new user
- If new user works, old user needs password re-hashing

### Step 4: Check Error Response
- Backend now returns `debug` field in error response
- Check Android logcat for debug message
- This will tell you exactly what's wrong

## 📋 Quick Checklist:

- [ ] User exists in database
- [ ] Email matches exactly (case-sensitive)
- [ ] Password is hashed with bcrypt (starts with `$2a$`, `$2b$`, or `$2y$`)
- [ ] User was created through signup endpoint (not manually)
- [ ] Backend logs show user found
- [ ] Backend logs show password verification result

## 🚀 Recommended Fix:

**Create a new user through the signup page** - This ensures:
- ✅ Password is properly hashed
- ✅ User is in correct table
- ✅ All fields are correct
- ✅ Login will work immediately

## 📞 Still Not Working?

1. **Check Railway backend logs** for detailed error messages
2. **Verify user in database** - check email, password format
3. **Create new user** through signup endpoint
4. **Check Android logcat** for error response debug messages

