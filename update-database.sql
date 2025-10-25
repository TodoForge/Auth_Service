-- Update database schema to match User entity
-- Run this script to update the users table

-- Add new columns to users table if they don't exist
ALTER TABLE users ADD COLUMN IF NOT EXISTS role_id UUID;
ALTER TABLE users ADD COLUMN IF NOT EXISTS organization_id UUID;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_valid BOOLEAN DEFAULT true;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS login_attempts INTEGER DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_picture VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS timezone VARCHAR(50) DEFAULT 'UTC';
ALTER TABLE users ADD COLUMN IF NOT EXISTS language VARCHAR(10) DEFAULT 'en';
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add foreign key constraint for role_id
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS fk_users_role 
    FOREIGN KEY (role_id) REFERENCES role(id);

-- Update existing records to have default values
UPDATE users SET is_active = true WHERE is_active IS NULL;
UPDATE users SET is_valid = true WHERE is_valid IS NULL;
UPDATE users SET email_verified = false WHERE email_verified IS NULL;
UPDATE users SET login_attempts = 0 WHERE login_attempts IS NULL;
UPDATE users SET timezone = 'UTC' WHERE timezone IS NULL;
UPDATE users SET language = 'en' WHERE language IS NULL;

-- Create role table if it doesn't exist
CREATE TABLE IF NOT EXISTS role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Create organizations table if it doesn't exist
CREATE TABLE IF NOT EXISTS organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    industry VARCHAR(100),
    company_size VARCHAR(50),
    website VARCHAR(255),
    address VARCHAR(1000),
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Insert ADMIN role if it doesn't exist
INSERT INTO role (id, name, description) 
VALUES (gen_random_uuid(), 'ADMIN', 'Administrator role')
ON CONFLICT (name) DO NOTHING;

-- Update existing users to have ADMIN role
UPDATE users SET role_id = (SELECT id FROM role WHERE name = 'ADMIN') 
WHERE role_id IS NULL;
