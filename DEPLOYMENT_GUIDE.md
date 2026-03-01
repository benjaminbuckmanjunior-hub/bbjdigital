# BBJ Church Manager - Deployment Guide

## Overview

This guide covers deploying the BBJ Church Manager application to both local development and production (TiDB Cloud) environments.

## System Requirements

### Local Development
- **Java**: JDK 11+
- **Apache Tomcat**: 9.0+
- **MySQL**: 8.0+ (running on localhost:1532 with credentials root/fire@1532)
- **Node.js**: 16+ (for frontend)
- **npm**: 8+

### Production
- **Java**: JDK 11+ (Linux recommended)
- **Apache Tomcat**: 9.0+
- **TiDB Cloud**: MySQL-compatible cloud database
- **Node.js**: 16+ (for frontend build)
- **Backblaze B2**: Cloud object storage account
- **HTTPS Certificate**: For production domain

---

## Part 1: Local Development Setup

### Step 1: Prepare Database

```bash
# Connect to local MySQL
mysql -u root -h localhost -P 1532 -pfire@1532

# Import database schema
USE mysql;
SOURCE c:/Users/Buckman/Desktop/BBJ digital/database/bbj.sql;
```

**Note**: The password for `root` user is empty by default on local MySQL.

### Step 2: Backend Configuration

1. **Set Environment Variable**
   ```bash
   # Windows (Command Prompt)
   set ENVIRONMENT=local
   
   # Windows (PowerShell)
   $env:ENVIRONMENT="local"
   
   # Linux/Mac
   export ENVIRONMENT=local
   ```

2. **Verify Backend Configuration Files**
   - `backend/src/config-local.properties` contains:
     - MySQL connection: `jdbc:mysql://localhost:1532/bbj?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
     - B2 credentials (same for local and production)
     - Local upload directory: `./uploads`

3. **Build Backend**
   ```bash
   cd backend
   mvn clean package
   # Creates: target/bbj-church-manager.war
   ```

4. **Deploy to Tomcat**
   ```bash
   # Copy WAR file
   cp backend/target/bbj-church-manager.war $TOMCAT_HOME/webapps/bbj.war
   
   # Restart Tomcat
   $TOMCAT_HOME/bin/startup.bat  # Windows
   # or
   $TOMCAT_HOME/bin/startup.sh   # Linux/Mac
   ```

   **Verify**: Open `http://localhost:8080/bbj/` in browser

### Step 3: Frontend Configuration

1. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Create .env.local File**
   ```
   REACT_APP_API_URL=http://localhost:8080/bbj/api
   REACT_APP_ENVIRONMENT=development
   ```

3. **Start Development Server**
   ```bash
   npm start
   # Opens: http://localhost:3000
   ```

### Step 4: Testing Local Deployment

**Test Checklist:**
- [ ] Frontend loads at `http://localhost:3000`
- [ ] Register new member: Name and auto-generated email
- [ ] Login as member with email/password
- [ ] View home page (announcements, featured events)
- [ ] View announcements list
- [ ] View events list
- [ ] View sermons list (if any exist)
- [ ] Login as admin
- [ ] Create announcement from admin dashboard
- [ ] Create event from admin dashboard
- [ ] Upload sermon file (MP3/MP4) via admin dashboard
- [ ] Verify file appears in Backblaze B2 console
- [ ] Download/play sermon file from member view
- [ ] Logout and verify redirect to login page

---

## Part 2: Production Deployment (TiDB Cloud)

### Step 1: Prepare TiDB Cloud Database

1. **Connection Details** (provided by user):
   - **Host**: `gateway01.eu-central-1.prod.aws.tidbcloud.com`
   - **Port**: `4000`
   - **Username**: `nrMPj1ECajN3NtY.root`
   - **Password**: `Gt19N5jWU7BMaDl5`
   - **Database**: `bbj`

2. **Create Database & Tables**
   ```bash
   # Connect to TiDB Cloud
   mysql -h gateway01.eu-central-1.prod.aws.tidbcloud.com \
         -P 4000 \
         -u "nrMPj1ECajN3NtY.root" \
         -p
   # When prompted, enter: Gt19N5jWU7BMaDl5
   
   # Execute SQL script
   SOURCE database/bbj.sql;
   ```

3. **Verify Connection**
   ```sql
   SHOW TABLES;
   -- Should display: admins, announcements, events, members, sermons
   ```

### Step 2: Backend Configuration for Production

1. **Set Environment Variable** (on production server)
   ```bash
   # Linux/Mac (in ~/.bashrc or ~/.bash_profile)
   export ENVIRONMENT=production
   
   # Windows Server (System Environment Variables)
   set ENVIRONMENT=production
   ```

2. **Verify Production Configuration**
   - `backend/src/config-production.properties` contains:
     - TiDB Cloud connection with `useSSL=true` and `serverTimezone=UTC`
     - B2 credentials (same as local)
     - Production upload directory: `/var/uploads`

3. **Build Backend**
   ```bash
   cd backend
   mvn clean package
   ```

4. **Deploy to Production Tomcat**
   ```bash
   # Copy WAR file to Tomcat on production server
   scp backend/target/bbj-church-manager.war \
       user@prod-server:/opt/tomcat/webapps/bbj.war
   
   # SSH into production server and restart Tomcat
   ssh user@prod-server
   /opt/tomcat/bin/shutdown.sh
   /opt/tomcat/bin/startup.sh
   ```

### Step 3: Frontend Configuration for Production

1. **Create .env.production File**
   ```
   REACT_APP_API_URL=https://your-domain.com/api
   REACT_APP_ENVIRONMENT=production
   ```

2. **Build Frontend for Production**
   ```bash
   cd frontend
   npm run build
   # Creates: build/ directory with optimized assets
   ```

3. **Serve Frontend**
   
   **Option A: Via Nginx (Recommended)**
   ```bash
   # Copy built files to Nginx web root
   sudo cp -r frontend/build/* /var/www/html/
   
   # Configure Nginx to proxy API calls to Tomcat
   # See nginx.conf configuration below
   ```
   
   **Option B: Via Tomcat**
   ```bash
   # Copy build directory to Tomcat
   cp -r frontend/build /opt/tomcat/webapps/bbj-frontend
   ```

4. **Nginx Configuration** (nginx.conf)
   ```nginx
   server {
       listen 80;
       server_name your-domain.com www.your-domain.com;
       
       # Redirect HTTP to HTTPS
       return 301 https://$server_name$request_uri;
   }
   
   server {
       listen 443 ssl;
       server_name your-domain.com www.your-domain.com;
       
       # SSL certificates
       ssl_certificate /etc/ssl/certs/your-domain.crt;
       ssl_certificate_key /etc/ssl/private/your-domain.key;
       
       # Serve frontend static files
       location / {
           root /var/www/html;
           try_files $uri $uri/ /index.html;
       }
       
       # Proxy API calls to Tomcat backend
       location /api/ {
           proxy_pass http://localhost:8080/bbj/api/;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```

### Step 4: Testing Production Deployment

**Pre-Deployment Tests:**
- [ ] TiDB Cloud database connection works: `mysql` command succeeds
- [ ] Create test table and verify query success
- [ ] Backend builds without errors: `mvn clean package`
- [ ] Frontend builds without errors: `npm run build`

**Post-Deployment Tests:**
- [ ] Frontend loads at `https://your-domain.com`
- [ ] API calls to `https://your-domain.com/api/` return data
- [ ] Register new member (writes to TiDB Cloud)
- [ ] Login works with stored credentials
- [ ] File upload to B2 succeeds (check B2 console)
- [ ] Monitor server logs for errors:
  ```bash
  tail -f /opt/tomcat/logs/catalina.out
  ```

---

## Part 3: Environment Configuration Files

### Backend Configuration Files

**Location**: `backend/src/`

**config-local.properties** (Development)
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:1532/bbj?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=

# Backblaze B2 Configuration
b2.keyId=5be753f9e33f
b2.applicationKey=K005uAur9WX0YGfPwOwbdyCsVwKOhuA
b2.bucketName=bbj-church-media

# File Upload Settings
upload.dir=./uploads
upload.maxSize=524288000  # 500MB in bytes
```

**config-production.properties** (Production)
```properties
# Database Configuration
db.url=jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/bbj?useSSL=true&serverTimezone=UTC
db.username=nrMPj1ECajN3NtY.root
db.password=Gt19N5jWU7BMaDl5

# Backblaze B2 Configuration
b2.keyId=5be753f9e33f
b2.applicationKey=K005uAur9WX0YGfPwOwbdyCsVwKOhuA
b2.bucketName=bbj-church-media

# File Upload Settings
upload.dir=/var/uploads
upload.maxSize=524288000  # 500MB in bytes
```

### Frontend Configuration Files

**Location**: `frontend/`

**.env.local** (Development)
```
REACT_APP_API_URL=http://localhost:8080/bbj/api
REACT_APP_ENVIRONMENT=development
REACT_APP_FILE_UPLOAD_ENDPOINT=/api/sermons/upload
```

**.env.production** (Production)
```
REACT_APP_API_URL=https://your-domain.com/api
REACT_APP_ENVIRONMENT=production
REACT_APP_FILE_UPLOAD_ENDPOINT=/api/sermons/upload
```

---

## Part 4: Advanced Configuration

### Database Connection Pooling

For production, add connection pooling to `DBConnection.java`:

```java
// Add to imports
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// Add connection pool
private static HikariDataSource dataSource;

static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(getConnectionUrl());
    config.setUsername(getUsername());
    config.setPassword(getPassword());
    config.setMaximumPoolSize(20);
    config.setMinimumIdle(5);
    config.setConnectionTimeout(30000);
    dataSource = new HikariDataSource(config);
}

public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
}
```

**Add pom.xml dependency**:
```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

### Logging Configuration

Add Log4j2 instead of System.out.println:

**pom.xml**:
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.20.0</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>
```

**log4j2.xml** (in `backend/src/`):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Appenders>
        <File name="FileAppender" fileName="logs/app.log">
            <PatternLayout pattern="%d{ISO8601} [%t] %-5p %c{1} - %m%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

### SSL/HTTPS Setup

For local testing:
```bash
# Generate self-signed certificate
keytool -genkey -alias tomcat -keyalg RSA -keystore keystore.jks \
        -keysize 2048 -validity 365

# Configure in Tomcat's server.xml
# <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
#            keystoreFile="keystore.jks" keystorePass="password"
#            keyAlias="tomcat"/>
```

For production:
- Use Let's Encrypt (free) or valid SSL certificate
- Configure in Nginx (preferred) or Tomcat
- Set `REACT_APP_API_URL` to HTTPS endpoint

---

## Part 5: Monitoring & Troubleshooting

### Common Issues

**1. MySQL Connection Fails (Local)**
```
Error: Communications link failure
```
- **Check**: Is MySQL running? `mysql -u root`
- **Fix**: Start MySQL service and verify port 1532, check credentials (root/fire@1532)
- **Verify**: Update config-local.properties if using different port

**2. TiDB Cloud Connection Fails (Production)**
```
Error: SSL connection error
```
- **Check**: Is `useSSL=true` in connection string?
- **Fix**: Ensure config-production.properties has SSL parameters
- **Verify**: Connection string matches exactly: `gateway01.eu-central-1.prod.aws.tidbcloud.com:4000`

**3. File Upload Fails**
```
Error: 500 Internal Server Error on /api/sermons/upload
```
- **Check**: Is Backblaze B2 account active?
- **Fix**: Verify B2 credentials in config files
- **Verify**: Upload directory exists: `./uploads` (local) or `/var/uploads` (production)

**4. Frontend API Calls Fail**
```
Error: CORS policy - blocked by browser
```
- **Check**: Is backend running? `curl http://localhost:8080/bbj/api/members`
- **Fix**: Add CORS headers to backend (already done in FileUploadServlet)
- **Verify**: REACT_APP_API_URL matches backend base URL

### Health Checks

**Backend Health Check**:
```bash
curl http://localhost:8080/bbj/api/members \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test"}'
```

**Frontend Health Check**:
```bash
curl http://localhost:3000/
# Should return HTML page
```

**Database Health Check**:
```bash
mysql -u root -h localhost -e "SELECT COUNT(*) FROM bbj.members;"
```

### Performance Monitoring

**View Tomcat Logs**:
```bash
tail -f /opt/tomcat/logs/catalina.out
tail -f /opt/tomcat/logs/catalina.2024-01-15.log
```

**Monitor TiDB Cloud**:
1. Go to TiDB Cloud Console
2. Select cluster → Monitoring
3. Check: Query Performance, Connections, Storage

**Monitor B2 Storage**:
1. Go to Backblaze B2 Console
2. Select bucket → Activity & Audit Log
3. Check: Upload/Download activity, file count

---

## Part 6: Backup & Disaster Recovery

### Database Backups

**Local MySQL**:
```bash
mysqldump -u root bbj > bbj_backup_$(date +%Y%m%d).sql
```

**TiDB Cloud**:
```bash
# Automated backups enabled by default
# Manual export via TiDB Cloud Console:
# 1. Cluster → Backup & Restore
# 2. Click "Backup" button
```

### File Backups

**B2 Versioning** (Recommended):
1. Go to B2 Console → Bucket Settings
2. Set "Enable File Versioning"
3. Files automatically retained with version history

**Local Directory**:
```bash
tar -czf sermon_files_backup.tar.gz ./uploads/
```

---

## Checklist Before Going Live

- [ ] Database tables created and populated
- [ ] Backend builds without errors
- [ ] Frontend builds without errors
- [ ] Environment variables set correctly for environment
- [ ] Configuration files have correct credentials
- [ ] HTTPS certificate obtained and configured
- [ ] Database backups automated
- [ ] File upload directory created and writable
- [ ] Tomcat memory settings tuned (production): `-Xmx2g`
- [ ] All endpoints tested locally
- [ ] Performance tested with sample data
- [ ] Monitoring setup (logs, metrics)
- [ ] Disaster recovery plan documented
- [ ] Team trained on deployment process

---

## Support

For issues or questions:
1. Check logs: `tail -f /opt/tomcat/logs/catalina.out`
2. Review error messages in browser console (Frontend) and Network tab
3. Test database connection: `mysql -u root -h localhost bbj`
4. Verify environment variables: `echo $ENVIRONMENT`

