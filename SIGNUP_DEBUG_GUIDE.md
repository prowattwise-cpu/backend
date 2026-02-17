# Signup Debug Guide

## 🔴 Problem: Signup says successful but data doesn't appear in database

### What I Fixed:

1. **Added Insert Verification**
   - Now checks `result.affectedRows` to ensure rows were actually inserted
   - Verifies `result.insertId` exists
   - Throws error if insert didn't affect any rows

2. **Added Post-Insert Verification**
   - After insert, queries the database to verify user was actually created
   - Shows which table was used
   - Logs UserID, Name, and Email

3. **Enhanced Logging**
   - Shows current database name
   - Shows which table was used for insert
   - Shows affected rows count
   - Verifies table exists after creation

4. **Better Error Handling**
   - Catches and logs all errors during insert
   - Shows detailed error messages
   - Returns specific error codes

## 🔍 How to Debug:

### Step 1: Check Railway Backend Logs

After deploying the updated backend, check Railway logs when you try to signup:

1. **Go to Railway Dashboard** → Backend Service → Deployments → View Logs
2. **Look for these messages during signup:**

**On Server Start:**
```
📊 Current database: [database_name]
✅ User table ready
✅ Verified: User table exists
📋 User table has X columns
```

**During Signup:**
```
🔍 Signup attempt for email: [email]
📊 Existing users with this email: 0
🔐 Password hashed successfully
📝 Attempting to insert user into 'User' table...
✅ User created in 'User' table with ID: [id]
📊 Affected rows: 1
🔍 Verifying user was created in 'User' table...
✅ Verification successful: User found in database
   UserID: [id]
   Name: [name]
   Email: [email]
✅ Signup successful for: [name] ([email])
   UserID: [id]
   Table: User
```

### Step 2: Check What's Happening

**If you see:**
- `✅ User created in 'User' table` → Data is in `User` table (uppercase)
- `✅ User created in 'user' table` → Data is in `user` table (lowercase)
- `❌ Verification failed: User not found` → Insert succeeded but user not found (possible database mismatch)
- `⚠️ Insert returned but no rows affected` → Insert failed silently

### Step 3: Verify Database and Table

1. **Check which database is being used:**
   - Look for: `📊 Current database: [name]`
   - Make sure it matches the database you're checking

2. **Check which table was used:**
   - Look for: `Table: User` or `Table: user`
   - Make sure you're checking the correct table

3. **Check if table exists:**
   - Look for: `✅ Verified: User table exists`
   - If you see warning, table might not exist

### Step 4: Query Database Directly

1. **Connect to Railway MySQL:**
   - Go to Railway → MySQL Service → Connect
   - Use Railway's database proxy or MySQL client

2. **Check current database:**
   ```sql
   SELECT DATABASE();
   ```

3. **Check which tables exist:**
   ```sql
   SHOW TABLES;
   ```

4. **Check both table names:**
   ```sql
   -- Check uppercase table
   SELECT * FROM User;
   
   -- Check lowercase table
   SELECT * FROM user;
   ```

5. **Check specific user:**
   ```sql
   -- In User table
   SELECT * FROM User WHERE Email = 'your@email.com';
   
   -- In user table
   SELECT * FROM user WHERE Email = 'your@email.com';
   ```

## 🐛 Common Issues:

### Issue 1: Wrong Database
**Symptom:** User created but not visible
**Solution:** Check which database the backend is using vs. which one you're checking

### Issue 2: Wrong Table Name
**Symptom:** User created in `User` but you're checking `user` (or vice versa)
**Solution:** Check logs to see which table was used, then check that table

### Issue 3: Insert Failing Silently
**Symptom:** Success message but no data
**Solution:** Check logs for `⚠️ Insert returned but no rows affected` or verification failures

### Issue 4: Transaction Not Committed
**Symptom:** Insert succeeds but data disappears
**Solution:** Check if there are transaction issues (unlikely with mysql2/promise)

## ✅ What to Check:

1. **Railway Backend Logs** - Check detailed signup logs
2. **Database Name** - Verify backend is using correct database
3. **Table Name** - Check both `User` and `user` tables
4. **Verification Query** - Backend now verifies user was created
5. **Affected Rows** - Backend checks if rows were actually inserted

## 📋 Next Steps:

1. **Deploy updated backend** to Railway
2. **Try signing up** a new user
3. **Check Railway logs** for detailed information
4. **Verify in database** using the table name shown in logs
5. **Check both tables** if unsure which one was used

## 🚀 Expected Behavior:

After the fix, you should see:
- ✅ Detailed logging showing insert success
- ✅ Verification query confirming user exists
- ✅ Response includes `table` field showing which table was used
- ✅ Clear error messages if something fails

The backend will now tell you exactly where the user was created and verify it was actually saved!

