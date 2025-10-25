-- PostgreSQL Database Setup Script for Auth Service
-- Run this script as a PostgreSQL superuser

-- Create Database
CREATE DATABASE auth_db;

-- Create User
CREATE USER auth_user WITH PASSWORD 'auth_password';

-- Grant Permissions
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;

-- Connect to the database
\c auth_db;

-- Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable Trigram Extension for Full-Text Search
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Grant Schema Permissions
GRANT ALL ON SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO auth_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO auth_user;

-- Set Default Privileges
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO auth_user;

-- Verify Setup
SELECT 'Database setup completed successfully!' as status;
