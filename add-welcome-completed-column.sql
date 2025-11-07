-- Add welcome_completed column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS welcome_completed BOOLEAN DEFAULT FALSE;

-- Update existing users to have welcome_completed = false
UPDATE users SET welcome_completed = FALSE WHERE welcome_completed IS NULL;

-- Make the column NOT NULL after setting default values
ALTER TABLE users ALTER COLUMN welcome_completed SET NOT NULL;

-- Verify the column was added
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name = 'welcome_completed';

