-- PostgreSQL Database Setup Script for Todoist-Style Auth Service
-- Run this script as a PostgreSQL superuser

-- Create Database (if it doesn't exist)
CREATE DATABASE auth_service;

-- Create User (if it doesn't exist)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'auth_user') THEN
        CREATE USER auth_user WITH PASSWORD 'auth_password';
    END IF;
END
$$;

-- Grant Permissions
GRANT ALL PRIVILEGES ON DATABASE auth_service TO auth_user;

-- Connect to the database
\c auth_service;

-- Enable Required Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Grant Schema Permissions
GRANT ALL ON SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO auth_user;

-- Set Default Privileges for Future Objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO auth_user;

-- Create Basic Tables (Hibernate will handle the rest)
-- This is just for initial setup - the application will create the full schema

-- Create role table
CREATE TABLE IF NOT EXISTS role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Insert default ADMIN role
INSERT INTO role (id, name, description) 
VALUES (gen_random_uuid(), 'ADMIN', 'Administrator role')
ON CONFLICT (name) DO NOTHING;

-- Verify Setup
SELECT 
    'Database setup completed successfully!' as status,
    'Database: ' || current_database() as database_name,
    'User: ' || current_user as current_user,
    'Extensions: ' || string_agg(extname, ', ') as extensions
FROM pg_extension 
WHERE extname IN ('uuid-ossp', 'pg_trgm');
