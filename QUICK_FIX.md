# Quick Database Fix

## Problem
The `setup_completed` column is missing from the `users` table in the database.

## Solution
Run this SQL command in your PostgreSQL database:

```sql
ALTER TABLE users ADD COLUMN setup_completed BOOLEAN DEFAULT FALSE;
```

## How to run it:

### Option 1: Using psql command line
```bash
psql -h localhost -U todoist_user -d todoist_db -c "ALTER TABLE users ADD COLUMN setup_completed BOOLEAN DEFAULT FALSE;"
```

### Option 2: Using pgAdmin or any PostgreSQL client
1. Connect to your database
2. Run the SQL command:
```sql
ALTER TABLE users ADD COLUMN setup_completed BOOLEAN DEFAULT FALSE;
```

### Option 3: Using the fix-database.sql file
```bash
psql -h localhost -U todoist_user -d todoist_db -f fix-database.sql
```

## After running the fix:
1. Restart your Auth-Service application
2. Try the registration flow again
3. The setup_completed column will now exist and the error should be resolved
