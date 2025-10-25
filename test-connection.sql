-- Test PostgreSQL Connection
-- Run this to verify database connection

-- Test Connection
SELECT 'PostgreSQL Connection Successful!' as status;

-- Test UUID Extension
SELECT uuid_generate_v4() as sample_uuid;

-- Test Database Info
SELECT 
    current_database() as database_name,
    current_user as current_user,
    version() as postgresql_version;

-- Test Permissions
SELECT 
    has_database_privilege('auth_user', 'auth_db', 'CONNECT') as can_connect,
    has_database_privilege('auth_user', 'auth_db', 'CREATE') as can_create;

-- Test Schema Access
SELECT 
    schemaname,
    tablename 
FROM pg_tables 
WHERE schemaname = 'public';
