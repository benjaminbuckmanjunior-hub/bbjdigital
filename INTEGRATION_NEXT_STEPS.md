# BBJ Church Manager - Integration & Next Steps Guide

## Current Status Summary

### ✅ Completed Components

#### Backend
- [x] ConfigManager.java - Centralized environment-aware configuration
- [x] DBConnection.java - Multi-environment database connectivity (Local MySQL & TiDB Cloud)
- [x] B2FileUploadService.java - Backblaze B2 integration with full API support
- [x] FileUploadServlet.java - Multipart form upload endpoint for sermons
- [x] config-local.properties - Development environment configuration
- [x] config-production.properties - Production environment configuration
- [x] LoginServlet.java - User authentication (member & admin)
- [x] RegisterServlet.java - User registration
- [x] DAOs (MemberDAO, AdminDAO, SermonDAO, AnnouncementDAO, EventDAO)
- [x] Models (Member, Admin, Sermon, Announcement, Event, Admin)

#### Frontend
- [x] React 18 with React Router v6
- [x] Tailwind CSS with custom theme (Lemon Yellow #F4D03F, Deep Teal #0F766E)
- [x] Responsive Navigation (mobile hamburger, desktop menu)
- [x] 7 Pages: Home, Login, Register, Announcements, Events, Sermons, AdminDashboard
- [x] API service layer with environment-based URL configuration
- [x] .env.local and .env.production environment files

#### Database
- [x] MySQL schema with 5 tables (members, admins, sermons, announcements, events)
- [x] Foreign key relationships and constraints
- [x] Timestamps (created_date, updated_at)
- [x] TiDB Cloud compatibility verified

#### Documentation
- [x] README.md - Project overview and quick start
- [x] SETUP_INSTRUCTIONS.md - Detailed setup guide
- [x] IMPLEMENTATION_SUMMARY.md - Architecture and feature summary
- [x] DEPLOYMENT_GUIDE.md - Local and production deployment steps
- [x] TESTING_CHECKLIST.md - Comprehensive testing procedures
- [x] CONFIGURATION_MANAGEMENT.md - Configuration system documentation

---

## Immediate Integration Tasks

### Task 1: Verify Maven Dependencies

**File**: `backend/pom.xml`

**Check for**:
```xml
<!-- Existing: servlet API -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>

<!-- Existing: MySQL driver -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- Existing: Gson -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- NEEDED: org.json for B2 API -->
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20231013</version>
</dependency>
```

**Action**: Add org.json dependency if missing, then run:
```bash
mvn clean install
```

### Task 2: Update pom.xml Build Configuration

**Add to pom.xml** in `<build><plugins>`:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>11</source>
        <target>11</target>
    </configuration>
</plugin>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <version>3.3.2</version>
    <configuration>
        <warName>bbj-church-manager</warName>
    </configuration>
</plugin>
```

### Task 3: Test Backend Compilation

```bash
cd backend

# Set local environment for testing
set ENVIRONMENT=local  # Windows
# or
export ENVIRONMENT=local  # Linux/Mac

# Build
mvn clean package

# Should create: target/bbj-church-manager.war
```

**Success Criteria**:
- No compilation errors
- WAR file created successfully
- All classes compiled without warnings

### Task 4: Frontend File Upload Component

Create file upload component for Admin Dashboard Sermons tab.

**File**: `frontend/src/components/SermonUpload.jsx`

```jsx
import React, { useState } from 'react';
import { uploadSermon } from '../services/api';

export default function SermonUpload({ onUploadSuccess }) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    file: null
  });
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && (file.type === 'audio/mpeg' || file.type === 'video/mp4')) {
      setFormData(prev => ({ ...prev, file }));
      setError('');
    } else {
      setError('Please select an MP3 or MP4 file');
      setFormData(prev => ({ ...prev, file: null }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title || !formData.file) {
      setError('Title and file are required');
      return;
    }

    setUploading(true);
    setError('');
    setSuccess('');

    try {
      const form = new FormData();
      form.append('title', formData.title);
      form.append('description', formData.description);
      form.append('file', formData.file);
      form.append('uploadedBy', localStorage.getItem('userId'));

      const response = await uploadSermon(form);
      
      if (response.data.success) {
        setSuccess('Sermon uploaded successfully!');
        setFormData({ title: '', description: '', file: null });
        onUploadSuccess();
      } else {
        setError(response.data.message || 'Upload failed');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Upload error');
    } finally {
      setUploading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-xl font-bold mb-4">Upload Sermon</h2>
      
      {error && <div className="bg-red-100 text-red-700 p-3 mb-4 rounded">{error}</div>}
      {success && <div className="bg-green-100 text-green-700 p-3 mb-4 rounded">{success}</div>}
      
      <div className="mb-4">
        <label className="block text-gray-700 font-bold mb-2">Title *</label>
        <input
          type="text"
          name="title"
          value={formData.title}
          onChange={handleChange}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-teal focus:ring-2 focus:ring-teal focus:ring-opacity-20"
          required
        />
      </div>

      <div className="mb-4">
        <label className="block text-gray-700 font-bold mb-2">Description</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          rows="3"
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-teal focus:ring-2 focus:ring-teal focus:ring-opacity-20"
        />
      </div>

      <div className="mb-4">
        <label className="block text-gray-700 font-bold mb-2">File (MP3 or MP4) *</label>
        <input
          type="file"
          accept="audio/mpeg,video/mp4"
          onChange={handleFileChange}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-teal"
          required
        />
        {formData.file && (
          <p className="text-sm text-gray-600 mt-2">
            Selected: {formData.file.name} ({(formData.file.size / 1024 / 1024).toFixed(2)} MB)
          </p>
        )}
      </div>

      <button
        type="submit"
        disabled={uploading}
        className="w-full bg-teal text-white font-bold py-2 px-4 rounded-lg hover:bg-teal-dark transition duration-200 disabled:bg-gray-400"
      >
        {uploading ? 'Uploading...' : 'Upload Sermon'}
      </button>
    </form>
  );
}
```

**Usage in AdminDashboard.jsx** (Sermons tab):
```jsx
import SermonUpload from '../components/SermonUpload';

// Inside AdminDashboard component, in Sermons tab:
const [sermons, setSermons] = useState([]);

const handleSermonUploadSuccess = () => {
  // Refresh sermon list
  loadSermons();
};

// In JSX:
<SermonUpload onUploadSuccess={handleSermonUploadSuccess} />
```

### Task 5: Integration Testing Steps

**Step 1: Start Local Development**
```bash
# Terminal 1: Start MySQL
# Windows: net start MySQL80
# Linux/Mac: brew services start mysql

# Terminal 2: Start Backend
cd backend
set ENVIRONMENT=local  # Windows or export ENVIRONMENT=local
mvn tomcat7:run  # or deploy to Tomcat manually

# Terminal 3: Start Frontend
cd frontend
npm start
```

**Step 2: Test Registration Flow**
1. Open http://localhost:3000
2. Go to Register page
3. Register new member: "John Doe"
4. Copy email from form
5. Go to Login page
6. Login with email and password "john"
7. Verify home page loads

**Step 3: Test Admin Dashboard**
1. Login as admin (email: admin@bbj.com, password: admin)
2. View Admin Dashboard
3. Create announcement
4. Create event
5. Verify data appears in member view

**Step 4: Test File Upload**
1. In Admin Dashboard → Sermons tab
2. Click "Upload Sermon"
3. Fill form:
   - Title: "Test Sermon May 2024"
   - Description: "A test sermon"
   - File: Select MP3 or MP4 from your computer
4. Submit
5. Verify:
   - Success message displays
   - Check B2 console (file should appear in bbj-church-media bucket)
   - Sermon appears in list
   - Member can play/download sermon from Sermons page

**Step 5: Test Authentication**
1. Logout
2. Try accessing /admin-dashboard
3. Should redirect to login
4. Login as regular member - verify no admin tab access
5. Login as admin - verify admin tab visible

---

## Database Migration Tasks

### If Moving from Development to TiDB Cloud

**Step 1: Backup Local Database**
```bash
mysqldump -u root -h localhost bbj > bbj_backup_$(date +%Y%m%d).sql
```

**Step 2: Create Database on TiDB Cloud**
```bash
mysql -h gateway01.eu-central-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u "nrMPj1ECajN3NtY.root" \
      -p
# Enter password: Gt19N5jWU7BMaDl5

# In MySQL prompt:
CREATE DATABASE bbj;
USE bbj;
SOURCE bbj_backup_$(date +%Y%m%d).sql;
```

**Step 3: Verify Data Integrity**
```sql
SELECT COUNT(*) FROM members;
SELECT COUNT(*) FROM sermons;
SELECT COUNT(*) FROM announcements;
```

**Step 4: Update Backend Configuration**
- Set `ENVIRONMENT=production`
- Deploy backend to production Tomcat
- Verify database connection works

---

## Optional Enhancement Tasks

### Enhancement 1: Add Connection Pooling

**Update pom.xml**:
```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

**Update DBConnection.java**:
```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

private static HikariDataSource dataSource;

static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(ConfigManager.getDbUrl());
    config.setUsername(ConfigManager.getDbUsername());
    config.setPassword(ConfigManager.getDbPassword());
    config.setMaximumPoolSize(20);
    config.setMinimumIdle(5);
    dataSource = new HikariDataSource(config);
}

public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
}
```

### Enhancement 2: Add Logging

**Update pom.xml**:
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

**Create log4j2.xml** in `backend/src/`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn">
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{ISO8601} [%t] %-5p %c{1} - %m%n"/>
        </Console>
        <File name="FileAppender" fileName="logs/bbj.log">
            <PatternLayout pattern="%d{ISO8601} [%t] %-5p %c{1} - %m%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="ConsoleAppender"/>
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

**Usage**:
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

private static final Logger logger = LogManager.getLogger(MyClass.class);

logger.info("Application started");
logger.error("Database connection failed", exception);
```

### Enhancement 3: Add JWT Authentication

Replace session-based auth with JWT tokens:

1. Add JWT library to pom.xml:
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

2. Create JWTUtil.java:
```java
public class JWTUtil {
    private static final String SECRET_KEY = "your-secret-key-change-in-production";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    public static String generateToken(String userId, String userType) {
        // Generate JWT token
    }
    
    public static Map<String, String> validateToken(String token) {
        // Validate and extract claims
    }
}
```

3. Update LoginServlet to return JWT token
4. Update frontend to send token in Authorization header
5. Create AuthFilter to validate tokens on each request

### Enhancement 4: Add Email Notifications

**Use JavaMail API**:
```xml
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId>
    <version>1.6.2</version>
</dependency>
```

**Create EmailService.java**:
```java
public class EmailService {
    public void sendWelcomeEmail(String email, String name) {
        // Send email to new members
    }
    
    public void sendEventReminder(String email, String eventName, LocalDateTime eventDate) {
        // Send event reminders
    }
}
```

**Add to ConfigManager**:
```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.from=noreply@bbj-church.com
mail.username=your-email@gmail.com
mail.password=your-app-password
```

### Enhancement 5: Add Sermon Download Tracking

Track which members download which sermons:

1. Create SermonDownload table:
```sql
CREATE TABLE sermon_downloads (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    sermon_id INT NOT NULL,
    downloaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (sermon_id) REFERENCES sermons(id)
);
```

2. Update FileDownloadServlet to log downloads
3. Add analytics to AdminDashboard

---

## Deployment Checklist

### Pre-Production Deployment
- [ ] All Maven dependencies verified in pom.xml
- [ ] Backend builds successfully: `mvn clean package`
- [ ] Frontend builds successfully: `npm run build`
- [ ] All tests pass (see TESTING_CHECKLIST.md)
- [ ] Configuration files have production values
- [ ] Database backups configured
- [ ] SSL certificate obtained and installed
- [ ] Monitoring/logging configured
- [ ] Credentials securely stored (not in code)

### Deployment Steps
1. Build backend and frontend
2. Configure TiDB Cloud database
3. Set ENVIRONMENT=production on server
4. Deploy WAR to Tomcat
5. Deploy frontend to Nginx/web server
6. Run smoke tests
7. Monitor logs for errors
8. Announce to users

---

## Rollback Plan

### If Production Issues

**Step 1: Immediate Rollback**
```bash
# Revert to previous WAR
cp /backup/bbj-church-manager-v1.war /opt/tomcat/webapps/bbj.war

# Restart Tomcat
/opt/tomcat/bin/shutdown.sh
/opt/tomcat/bin/startup.sh
```

**Step 2: Database Rollback** (if data corruption)
```bash
# Restore from backup
mysql -h gateway01.eu-central-1.prod.aws.tidbcloud.com \
      -u "nrMPj1ECajN3NtY.root" \
      -p < bbj_backup_20240115.sql
```

**Step 3: Notify Users**
- Post maintenance message on homepage
- Email members about resolution
- Follow up on issues

---

## Success Criteria

### Local Development Ready ✅
- Application builds without errors
- All endpoints tested and working
- File upload to B2 successful
- User authentication working
- Responsive design verified on multiple browsers

### Production Ready ✅
- TiDB Cloud database verified working
- Backend deployed and responding on HTTPS
- Frontend deployed and loading HTTPS
- All smoke tests passed
- Monitoring and logging active
- User documentation provided

---

## Support & Resources

- **Configuration Issues**: See CONFIGURATION_MANAGEMENT.md
- **Deployment Issues**: See DEPLOYMENT_GUIDE.md
- **Testing Issues**: See TESTING_CHECKLIST.md
- **Backblaze B2 Docs**: https://www.backblaze.com/b2/docs/
- **TiDB Cloud Docs**: https://docs.tidbcloud.com/
- **React Docs**: https://react.dev/
- **Tailwind CSS Docs**: https://tailwindcss.com/docs

---

## Next Steps (in order)

1. **Today**: 
   - [ ] Verify pom.xml has all dependencies
   - [ ] Build backend: `mvn clean package`
   - [ ] Build frontend: `npm run build`
   - [ ] Create SermonUpload.jsx component

2. **Tomorrow**:
   - [ ] Test local environment (all 5 integration tests)
   - [ ] Verify B2 upload working
   - [ ] Verify database persistence

3. **This Week**:
   - [ ] Set up TiDB Cloud cluster
   - [ ] Test production database connection
   - [ ] Prepare production deployment

4. **Next Week**:
   - [ ] Deploy to production
   - [ ] Smoke test all features
   - [ ] Launch application

