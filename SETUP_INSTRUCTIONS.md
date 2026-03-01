# BBJ Church Manager - Complete Setup Guide

## ✅ Quick Start Checklist

- [ ] Java JDK installed
- [ ] MySQL Server installed and running
- [ ] Node.js & npm installed
- [ ] Apache Tomcat installed (for backend deployment)
- [ ] Code editor ready (VS Code recommended)

---

## 🗄️ Step 1: Database Setup

### 1.1 Start MySQL Server
```bash
# Windows
net start MySQL80  # or your MySQL version

# macOS
brew services start mysql-server

# Linux
sudo systemctl start mysql
```

### 1.2 Create Database
```bash
# Open MySQL CLI
mysql -u root -p

# Enter your MySQL password when prompted
```

### 1.3 Execute Database Schema
```bash
# From MySQL CLI
source database/bbj.sql

# Verify tables created
USE bbj;
SHOW TABLES;
```

**Expected tables:**
- members
- admins
- sermons
- announcements
- events

---

## 🔧 Step 2: Backend Setup (Java)

### 2.1 Configure Database Connection
Edit `backend/src/db/DBConnection.java`:

```java
public static Connection getConnection() {
    try {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/bbj",  // ← Change port if needed
            "root",                             // ← Your MySQL username
            "your_password"                     // ← Your MySQL password
        );
    } catch(Exception e) {
        e.printStackTrace();
    }
    return null;
}
```

### 2.2 Add Required Dependencies
Ensure your `pom.xml` includes:
```xml
<!-- MySQL Connector -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- Gson for JSON -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- Servlet API -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0</version>
    <scope>provided</scope>
</dependency>
```

### 2.3 Build Backend
```bash
cd backend

# Clean build
mvn clean package

# Expected output: BUILD SUCCESS
# Check target/bbj.war created
```

### 2.4 Deploy to Tomcat
```bash
# Copy WAR file to Tomcat
cp target/bbj.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh  # Linux/macOS
# or
$TOMCAT_HOME/bin/startup.bat  # Windows

# Verify deployment
# Open browser: http://localhost:8080/bbj
```

**Troubleshooting:**
- If port 8080 is in use, change it in `$TOMCAT_HOME/conf/server.xml`
- Check `$TOMCAT_HOME/logs/catalina.out` for errors

---

## ⚛️ Step 3: Frontend Setup (React)

### 3.1 Install Dependencies
```bash
cd frontend

npm install
# or
yarn install
```

### 3.2 Update Backend API URL (if needed)
Edit `frontend/src/services/api.js`:

```javascript
export const API_URL = 'http://localhost:8080/api';
// Change if your backend is on different host/port
```

### 3.3 Start Development Server
```bash
npm start

# Opens http://localhost:3000 in your browser
# Hot reload enabled - changes update automatically
```

### 3.4 Build for Production
```bash
npm run build

# Creates optimized production build in 'build' folder
# Ready for deployment
```

---

## 🧪 Step 4: Testing the Application

### 4.1 Test Member Registration
1. Navigate to http://localhost:3000/register
2. Enter:
   - Name: "John Doe"
   - Password: "password123"
   - Confirm: "password123"
3. Click "Create Account"
4. ✅ Should see success message with auto-generated email

### 4.2 Test Member Login
1. Go to http://localhost:3000/login
2. Select "Member" radio button
3. Enter:
   - Email: (from registration)
   - Password: "password123"
4. ✅ Should redirect to home page

### 4.3 Test Admin Login
1. Go to http://localhost:3000/login
2. Select "Admin" radio button
3. Use admin credentials (add one in database):
   ```sql
   INSERT INTO admins (name, email, password) 
   VALUES ('Pastor John', 'pastor@bbj-church.local', 'admin123');
   ```
4. ✅ Should redirect to admin dashboard

### 4.4 Test Admin Dashboard
1. After admin login, go to /admin-dashboard
2. Test each tab:
   - **Members**: View all registered members
   - **Announcements**: Create and delete announcements
   - **Events**: Create and delete events
   - **Sermons**: Upload sermon metadata

### 4.5 Test Member Features
1. As member, test:
   - **Home**: View announcements and events preview
   - **Announcements**: View all announcements (read-only)
   - **Events**: Browse upcoming events
   - **Sermons**: View sermon list (click links to play/watch)

---

## 📊 Database Verification

### Check Members Table
```sql
SELECT * FROM bbj.members;
```

### Check Announcements
```sql
SELECT * FROM bbj.announcements;
```

### Count Records
```sql
SELECT 
    (SELECT COUNT(*) FROM members) as total_members,
    (SELECT COUNT(*) FROM admins) as total_admins,
    (SELECT COUNT(*) FROM announcements) as total_announcements,
    (SELECT COUNT(*) FROM events) as total_events,
    (SELECT COUNT(*) FROM sermons) as total_sermons;
```

---

## 🎨 Customization

### Change Theme Colors
Edit `frontend/tailwind.config.js`:

```javascript
theme: {
    extend: {
        colors: {
            lemon: '#F4D03F',        // Change yellow
            tealDeep: '#0F766E'      // Change teal
        }
    },
},
```

### Update Church Name
1. Edit `frontend/src/components/Navbar.jsx` - Change "BBJ"
2. Edit `frontend/src/pages/Home.jsx` - Change title
3. Edit `frontend/public/index.html` - Change page title
4. Edit `database/bbj.sql` - Add initial admin data

### Customize API Base URL
Edit `.env`:
```
REACT_APP_API_URL=http://your-domain.com/api
```

---

## 🚀 Deployment Guide

### Backend Deployment
1. Build WAR file: `mvn clean package`
2. Update database credentials for production
3. Deploy to Tomcat or other servlet container
4. Set environment variable: `JAVA_OPTS="-Duser.country=US"`

### Frontend Deployment

#### Option 1: Nginx
```bash
# Build
npm run build

# Copy to Nginx
cp -r build/* /usr/share/nginx/html/

# Restart Nginx
sudo systemctl restart nginx
```

#### Option 2: Apache
```bash
# Build
npm run build

# Copy to DocumentRoot
cp -r build/* /var/www/html/

# Enable rewrite module
a2enmod rewrite

# Restart Apache
systemctl restart apache2
```

#### Option 3: GitHub Pages / Vercel
```bash
npm run build
# Then deploy 'build' folder to your hosting
```

---

## 🔐 Security Checklist

- [ ] Use HTTPS in production
- [ ] Hash passwords appropriately
- [ ] Enable CORS only for trusted domains
- [ ] Use environment variables for secrets
- [ ] Implement rate limiting on API endpoints
- [ ] Validate all user inputs both frontend & backend
- [ ] Use prepared statements (already implemented)
- [ ] Keep dependencies updated

---

## 📞 Troubleshooting Guide

### "Cannot connect to database"
**Solution:**
- Check MySQL is running
- Verify port number (default 3306)
- Check username/password in DBConnection.java
- Run: `mysql -u root -p` to test connection

### "CORS Error - localhost refused to connect"
**Solution:**
- Verify backend is running on port 8080
- Update API_URL in api.js to match your backend
- Check firewall settings

### "npm start gives error"
**Solution:**
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
npm start
```

### "React won't load"
**Solution:**
```bash
# Clear browser cache (Ctrl+Shift+Del)
# Or use private/incognito mode
# Check console (F12) for errors
```

### "Maven build fails"
**Solution:**
```bash
# Update Maven
mvn --version

# Clear local repository
rm -rf ~/.m2/repository

# Rebuild
mvn clean install
```

---

## 📚 Project Files Overview

### Key Backend Files
- `DBConnection.java` - Database configuration
- `LoginServlet.java` - Authentication
- `AdminDashboard.jsx` - Admin interface
- `MemberDAO.java` - Member database operations

### Key Frontend Files
- `App.jsx` - Main routing
- `Navbar.jsx` - Navigation component
- `api.js` - API calls
- `tailwind.config.js` - Styling config

### Schema Files
- `bbj.sql` - Database schema
- `pom.xml` - Java dependencies
- `package.json` - Node dependencies

---

## ✨ Additional Features to Consider

1. **Email Notifications** - Notify members of new announcements
2. **User Profiles** - Allow members to update their info
3. **Search Functionality** - Search announcements/sermons
4. **File Upload** - Direct sermon file uploads (not just URLs)
5. **Statistics Dashboard** - Admin analytics
6. **Comments/Feedback** - Members can comment on announcements
7. **Event Registration** - Members can RSVP to events
8. **Mobile App** - React Native mobile version

---

## 🎓 Learning Resources

- [React Documentation](https://react.dev)
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [Java Servlets Guide](https://docs.oracle.com/javase/tutorial/servlet/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Axios Documentation](https://axios-http.com/docs)

---

## 📝 Notes for Development

- Backend runs on `http://localhost:8080`
- Frontend runs on `http://localhost:3000`
- Database runs on `localhost:3306` (or configured port)
- Auto-generated emails format: `name.timestamp@bbj-church.local`
- All passwords stored in database (implement hashing before production)
- CORS is open to all origins (restrict in production)

---

## 🎉 Success Indicators

✅ When fully set up, you should see:
1. Login page at http://localhost:3000/login
2. Registration page at http://localhost:3000/register
3. Home page with announcements and events
4. Admin dashboard with all management options
5. Responsive design on mobile devices
6. Clean lemon yellow and deep teal color scheme

---

**Happy developing! If you encounter issues, check the troubleshooting section or review the README.md for more details.** 🚀
