# PostgreSQL Setup Guide for Auth Service

## 🚀 Quick Setup Instructions

### **Step 1: Install PostgreSQL**

#### **Windows:**
```bash
# Option 1: Using Chocolatey (Recommended)
choco install postgresql

# Option 2: Download from official website
# Visit: https://www.postgresql.org/download/windows/
# Download and run the installer
```

#### **Alternative: Using Docker (Easiest)**
```bash
# Run PostgreSQL in Docker
docker run --name postgres-auth \
  -e POSTGRES_DB=auth_db \
  -e POSTGRES_USER=auth_user \
  -e POSTGRES_PASSWORD=auth_password \
  -p 5432:5432 \
  -d postgres:15
```

### **Step 2: Setup Database**

#### **Method 1: Using psql Command Line**
```bash
# Connect to PostgreSQL
psql -U postgres

# Run the setup script
\i database-setup.sql
```

#### **Method 2: Using pgAdmin (GUI)**
1. Open pgAdmin
2. Connect to PostgreSQL server
3. Right-click on "Databases" → "Create" → "Database"
4. Name: `auth_db`
5. Right-click on "auth_db" → "Query Tool"
6. Copy and paste the contents of `database-setup.sql`
7. Execute the script

### **Step 3: Verify Setup**

#### **Test Connection:**
```bash
# Connect to the database
psql -U auth_user -d auth_db -h localhost

# Run test script
\i test-connection.sql
```

#### **Expected Output:**
```
PostgreSQL Connection Successful!
sample_uuid: [UUID]
database_name: auth_db
current_user: auth_user
postgresql_version: PostgreSQL 15.x
can_connect: true
can_create: true
```

### **Step 4: Start Auth Service**

```bash
# Navigate to Auth Service directory
cd Auth-Service

# Run the application
mvn spring-boot:run
```

### **Step 5: Verify Application**

#### **Check Health:**
```bash
curl http://localhost:8082/api/auth/test
```

#### **Expected Response:**
```json
{
  "message": "Auth Service is working!"
}
```

## 🔧 Configuration Details

### **Database Configuration:**
- **Host:** localhost
- **Port:** 5432
- **Database:** auth_db
- **Username:** auth_user
- **Password:** auth_password

### **Connection Pool Settings:**
- **Max Pool Size:** 20
- **Min Idle:** 5
- **Idle Timeout:** 5 minutes
- **Max Lifetime:** 20 minutes
- **Connection Timeout:** 20 seconds

### **JPA/Hibernate Settings:**
- **DDL Auto:** update (creates tables automatically)
- **Show SQL:** false (no SQL queries in terminal)
- **Format SQL:** false
- **Dialect:** PostgreSQL

## 🚨 Troubleshooting

### **Common Issues:**

#### **1. Connection Refused:**
```bash
# Check if PostgreSQL is running
pg_ctl status

# Start PostgreSQL service
pg_ctl start
```

#### **2. Authentication Failed:**
```bash
# Reset password
psql -U postgres
ALTER USER auth_user PASSWORD 'auth_password';
```

#### **3. Database Not Found:**
```bash
# Create database manually
psql -U postgres
CREATE DATABASE auth_db;
```

#### **4. Permission Denied:**
```bash
# Grant permissions
psql -U postgres -d auth_db
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
```

## 📊 Performance Optimization

### **PostgreSQL Configuration:**
```sql
-- Increase shared_buffers (in postgresql.conf)
shared_buffers = 256MB

-- Increase work_mem
work_mem = 4MB

-- Enable logging for debugging (optional)
log_statement = 'all'
```

### **Connection Pool Tuning:**
```properties
# For high-traffic applications
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
```

## ✅ Verification Checklist

- [ ] PostgreSQL installed and running
- [ ] Database `auth_db` created
- [ ] User `auth_user` created with proper permissions
- [ ] Extensions (uuid-ossp, pg_trgm) enabled
- [ ] Auth Service starts without errors
- [ ] Database connection successful
- [ ] No SQL queries showing in terminal
- [ ] Health endpoint responding

## 🎯 Next Steps

1. **Test Registration:** Try registering a new user
2. **Test Login:** Try logging in with the registered user
3. **Check Database:** Verify tables are created in PostgreSQL
4. **Monitor Performance:** Check connection pool and query performance

---

**Your Auth Service is now ready with PostgreSQL! 🚀**
