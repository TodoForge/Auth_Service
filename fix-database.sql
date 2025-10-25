-- Connect to the database and run these commands:

-- 1. Add the missing setup_completed column
ALTER TABLE users ADD COLUMN IF NOT EXISTS setup_completed BOOLEAN DEFAULT FALSE;

-- 2. Update existing users to have setup_completed = false
UPDATE users SET setup_completed = FALSE WHERE setup_completed IS NULL;

-- 3. Make the column NOT NULL after setting default values
ALTER TABLE users ALTER COLUMN setup_completed SET NOT NULL;

-- 4. Verify the column was added
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name = 'setup_completed';

-- 5. Check if there are any existing users and their setup status
SELECT id, email, setup_completed FROM users LIMIT 5;
