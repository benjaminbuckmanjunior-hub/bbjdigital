# BBJ Church Manager - Configuration Management Guide

## Overview

The BBJ Church Manager application uses a flexible, environment-based configuration system that allows seamless deployment across multiple environments (local development, staging, production) without code changes.

---

## Architecture

### Configuration Hierarchy

```
Application
    ↓
ConfigManager.java (centralized access)
    ↓
Environment Variable: ENVIRONMENT
    ↓
    ├→ local → config-local.properties
    └→ production → config-production.properties
```

### Key Components

#### 1. ConfigManager.java
**Location**: `backend/src/com/example/config/ConfigManager.java`

Central management class that handles:
- Reading environment-specific property files
- Caching configuration after first load
- Providing static getters for all config values
- Falling back to defaults if values not found

**Usage**:
```java
import com.example.config.ConfigManager;

// Get database URL
String dbUrl = ConfigManager.getDbUrl();

// Get B2 credentials
String keyId = ConfigManager.getB2KeyId();
String appKey = ConfigManager.getB2ApplicationKey();
String bucketName = ConfigManager.getB2BucketName();

// Get upload configuration
String uploadDir = ConfigManager.getUploadDirectory();
long maxSize = ConfigManager.getMaxUploadSize();
```

#### 2. Property Files
Located at: `backend/src/`

**File Structure**:
```
backend/src/
├── config-local.properties      # Development environment
├── config-production.properties # Production environment
├── com/example/
│   ├── config/
│   │   └── ConfigManager.java
│   ├── db/
│   │   └── DBConnection.java
│   ├── servlet/
│   │   └── FileUploadServlet.java
│   ├── service/
│   │   └── B2FileUploadService.java
│   └── dao/
│       └── ...
```

#### 3. Environment Detection
ConfigManager determines which properties file to load:

```java
private static final String ENVIRONMENT = 
    System.getenv("ENVIRONMENT") != null ? 
    System.getenv("ENVIRONMENT") : 
    "local";  // Default to local if not set

private static final String CONFIG_FILE = 
    "config-" + ENVIRONMENT + ".properties";
```

**Default**: If `ENVIRONMENT` variable not set, uses "local" configuration

---

## Configuration Properties

### Database Configuration

#### Local (config-local.properties)
```properties
db.url=jdbc:mysql://localhost:3306/bbj
db.username=root
db.password=
```

#### Production (config-production.properties)
```properties
db.url=jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/bbj?useSSL=true&serverTimezone=UTC
db.username=nrMPj1ECajN3NtY.root
db.password=Gt19N5jWU7BMaDl5
```

**Properties**:
- `db.url`: JDBC connection string with all parameters
- `db.username`: Database user account
- `db.password`: Database password (empty for local development)

**Notes**:
- TiDB Cloud requires `useSSL=true` for secure connections
- `serverTimezone=UTC` prevents timezone conversion issues
- Local MySQL uses custom port 1532 with password: fire@1532

### Backblaze B2 Configuration

**Both environments** (same credentials):
```properties
b2.keyId=5be753f9e33f
b2.applicationKey=K005uAur9WX0YGfPwOwbdyCsVwKOhuA
b2.bucketName=bbj-church-media
```

**Properties**:
- `b2.keyId`: Master API Key ID (authorize.bucket_id)
- `b2.applicationKey`: Master Application Key (secret for authorization)
- `b2.bucketName`: Bucket name where files are stored

**Important**: Keep these credentials secure; never commit to version control

### File Upload Configuration

#### Local (config-local.properties)
```properties
upload.dir=./uploads
upload.maxSize=524288000
```

#### Production (config-production.properties)
```properties
upload.dir=/var/uploads
upload.maxSize=524288000
```

**Properties**:
- `upload.dir`: Temporary directory for uploading files before B2
- `upload.maxSize`: Maximum file size in bytes (500MB = 524288000)

**Directory Permissions** (Production):
```bash
# Create upload directory
sudo mkdir -p /var/uploads
sudo chown tomcat:tomcat /var/uploads
sudo chmod 755 /var/uploads
```

---

## Usage Examples

### Example 1: Database Connection

**In DBConnection.java**:
```java
import com.example.config.ConfigManager;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = ConfigManager.getDbUrl();
        String username = ConfigManager.getDbUsername();
        String password = ConfigManager.getDbPassword();
        
        return DriverManager.getConnection(url, username, password);
    }
}
```

**Runtime behavior**:
- If `ENVIRONMENT=local`: Connects to localhost:1532 (root/fire@1532)
- If `ENVIRONMENT=production`: Connects to TiDB Cloud with SSL
- If unset: Defaults to local

### Example 2: B2 File Upload

**In B2FileUploadService.java**:
```java
import com.example.config.ConfigManager;

public class B2FileUploadService {
    private String keyId;
    private String applicationKey;
    private String bucketName;
    
    public B2FileUploadService() {
        this.keyId = ConfigManager.getB2KeyId();
        this.applicationKey = ConfigManager.getB2ApplicationKey();
        this.bucketName = ConfigManager.getB2BucketName();
    }
    
    public void uploadFile(File file) throws IOException {
        // Use keyId, applicationKey, bucketName
        // Credentials loaded from environment-specific property file
    }
}
```

### Example 3: File Upload Directory

**In FileUploadServlet.java**:
```java
import com.example.config.ConfigManager;

public void doPost(HttpServletRequest request, HttpServletResponse response) {
    String uploadDir = ConfigManager.getUploadDirectory();
    long maxSize = ConfigManager.getMaxUploadSize();
    
    File uploadPath = new File(uploadDir);
    if (!uploadPath.exists()) {
        uploadPath.mkdirs();
    }
    
    // Process upload with validation against maxSize
}
```

---

## Setting Environment Variables

### Windows Development

**Option 1: Command Prompt (temporary)**
```batch
set ENVIRONMENT=local
mvn clean package
```

**Option 2: PowerShell (temporary)**
```powershell
$env:ENVIRONMENT="local"
mvn clean package
```

**Option 3: Permanent (System Environment Variables)**
1. Press `Win + X` → System
2. Click "Advanced system settings"
3. Environment Variables → New
4. Variable name: `ENVIRONMENT`
5. Variable value: `local`
6. OK → OK → Restart IDE/Terminal

**Verify**:
```batch
echo %ENVIRONMENT%  # Should output: local
```

### Linux/Mac Development

**Option 1: Temporary (current session)**
```bash
export ENVIRONMENT=local
mvn clean package
```

**Option 2: Permanent (in ~/.bashrc or ~/.bash_profile)**
```bash
echo 'export ENVIRONMENT=local' >> ~/.bashrc
source ~/.bashrc
```

**Verify**:
```bash
echo $ENVIRONMENT  # Should output: local
```

### Production Server

**Linux/Mac**:
```bash
# Edit /etc/environment
sudo nano /etc/environment

# Add line:
ENVIRONMENT=production

# Save and logout/login for changes to take effect
```

**Or in Tomcat startup script** (`catalina.sh`):
```bash
export ENVIRONMENT=production
```

**Verify on server**:
```bash
echo $ENVIRONMENT  # Should output: production
```

---

## Adding New Configuration Properties

### Step 1: Add to Property Files

**Update config-local.properties**:
```properties
# New property
myapp.hostname=localhost
myapp.debug=true
```

**Update config-production.properties**:
```properties
# New property
myapp.hostname=api.bbj-church.com
myapp.debug=false
```

### Step 2: Add Getter to ConfigManager

**In ConfigManager.java**:
```java
public static String getHostname() {
    return getProperty("myapp.hostname");
}

public static boolean isDebugMode() {
    String debug = getProperty("myapp.debug");
    return "true".equalsIgnoreCase(debug);
}
```

### Step 3: Use in Code

```java
import com.example.config.ConfigManager;

public class MyService {
    public void initialize() {
        String hostname = ConfigManager.getHostname();
        boolean debug = ConfigManager.isDebugMode();
        
        if (debug) {
            System.out.println("Running on: " + hostname);
        }
    }
}
```

### Step 4: Test Both Environments

```bash
# Test local
set ENVIRONMENT=local
mvn clean package

# Test production
set ENVIRONMENT=production
mvn clean package
```

---

## Best Practices

### 1. Security
✅ **DO**:
- Store sensitive data (passwords, API keys) in property files
- Keep property files out of version control (add to .gitignore)
- Use different credentials for each environment
- Rotate credentials periodically

❌ **DON'T**:
- Hardcode credentials in source code
- Commit property files to git
- Reuse production credentials in development
- Store passwords in plain text in version control

### 2. Defaults
✅ **DO**:
- Provide sensible defaults in ConfigManager (e.g., "local" environment)
- Use Optional or null checks for missing values
- Log configuration on startup for debugging

❌ **DON'T**:
- Assume property files always exist
- Throw exceptions if optional properties missing
- Silently fail with vague error messages

### 3. Naming Convention
✅ **DO**:
- Use hierarchical names: `db.url`, `b2.keyId`, `upload.dir`
- Use consistent separators: lowercase words separated by dots
- Document each property with comments

❌ **DON'T**:
- Use abbreviations that aren't obvious
- Mix naming conventions (camelCase vs snake_case)
- Create properties without documentation

### 4. Validation
✅ **DO**:
- Validate property values on load (URLs, ports, file paths)
- Ensure required properties exist
- Test configuration changes before deployment

❌ **DON'T**:
- Assume property values are valid
- Deploy with incomplete configuration
- Ignore validation errors

---

## Troubleshooting

### Issue: "Cannot find config file"

**Symptom**: `java.io.FileNotFoundException: config-local.properties`

**Solution**:
1. Verify property file exists in `backend/src/`
2. Check file encoding: should be UTF-8
3. Verify path is correct (case-sensitive on Linux/Mac)
4. Rebuild and redeploy:
   ```bash
   mvn clean package
   ```

### Issue: Properties returning null

**Symptom**: `ConfigManager.getDbUrl()` returns null

**Solution**:
1. Verify ENVIRONMENT variable is set:
   ```bash
   echo $ENVIRONMENT  # or echo %ENVIRONMENT% on Windows
   ```
2. Verify property file has the property:
   ```bash
   grep "db.url" backend/src/config-*.properties
   ```
3. Verify ConfigManager reloaded after property file change

### Issue: Wrong environment being used

**Symptom**: Application connecting to production when ENVIRONMENT=local

**Solution**:
1. Verify ENVIRONMENT variable:
   ```bash
   echo $ENVIRONMENT
   ```
2. If using IDE, set environment variable in Run Configuration:
   - IntelliJ: Run → Edit Configurations → Environment variables
   - Eclipse: Run → Run Configurations → Arguments → VM arguments: `-DENVIRONMENT=local`
3. Verify system environment variable (not just shell variable)
4. Rebuild and restart:
   ```bash
   mvn clean package
   ./startup.sh
   ```

### Issue: Database connection fails in production

**Symptom**: `Communications link failure` on production

**Solution**:
1. Verify TiDB Cloud cluster is running (check TiDB Console)
2. Verify connection string has useSSL parameters:
   ```properties
   db.url=jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/bbj?useSSL=true&serverTimezone=UTC
   ```
3. Verify credentials are correct
4. Test connection from command line:
   ```bash
   mysql -h gateway01.eu-central-1.prod.aws.tidbcloud.com \
         -P 4000 \
         -u "nrMPj1ECajN3NtY.root" \
         -p
   ```

### Issue: B2 credentials not working

**Symptom**: `401 Unauthorized` when uploading to B2

**Solution**:
1. Verify B2 Account is active (check B2 web console)
2. Verify credentials in property file:
   ```bash
   grep "b2\." backend/src/config-*.properties
   ```
3. Verify API Key hasn't been revoked (B2 Console → Account → API Tokens)
4. Test B2 connection:
   ```java
   B2FileUploadService service = new B2FileUploadService();
   service.testConnection();  // Should print success/failure
   ```

---

## Configuration Checklists

### Pre-Development Checklist
- [ ] ENVIRONMENT=local set in IDE
- [ ] config-local.properties exists with all required properties
- [ ] MySQL running on localhost:1532 with correct credentials
- [ ] B2 credentials valid and tested
- [ ] Upload directory writable (./uploads)

### Pre-Production Checklist
- [ ] ENVIRONMENT=production set on server
- [ ] config-production.properties exists with all required properties
- [ ] TiDB Cloud cluster running and database created
- [ ] B2 credentials same as local (or updated in production property file)
- [ ] /var/uploads directory exists and writable by Tomcat user
- [ ] SSL certificates configured
- [ ] Backups configured (TiDB Cloud automated backups enabled)

---

## References

- [Java Properties API Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html)
- [TiDB Cloud JDBC Connection String](https://docs.tidbcloud.com/tidbcloud/jdbc-driver-connection-parameters)
- [Backblaze B2 API Documentation](https://www.backblaze.com/b2/docs/b2_api_overview.html)
- [Apache Tomcat Environment Variables](https://tomcat.apache.org/tomcat-9.0-doc/config/index.html)

