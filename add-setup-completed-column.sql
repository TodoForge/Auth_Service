-- Add setup_completed column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS setup_completed BOOLEAN DEFAULT FALSE;

-- Update existing users to have setup_completed = false
UPDATE users SET setup_completed = FALSE WHERE setup_completed IS NULL;

-- Make the column NOT NULL after setting default values
ALTER TABLE users ALTER COLUMN setup_completed SET NOT NULL;

-- Verify the column was added
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name = 'setup_completed';
