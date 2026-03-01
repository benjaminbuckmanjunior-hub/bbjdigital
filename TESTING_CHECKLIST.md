# BBJ Church Manager - Testing Checklist

## Overview
This document provides a comprehensive testing checklist for local development and production environments.

---

## Phase 1: Pre-Deployment Setup Tests

### Database Setup
- [ ] MySQL is running on localhost:1532 (credentials: root / fire@1532)
- [ ] Database `bbj` exists
- [ ] All 5 tables created: `members`, `admins`, `sermons`, `announcements`, `events`
- [ ] Test query succeeds:
  ```bash
  mysql -u root -h localhost -P 1532 -pfire@1532 -e "USE bbj; SHOW TABLES;"
  ```

### Backend Setup
- [ ] ENVIRONMENT variable set to `local`:
  ```bash
  echo $ENVIRONMENT  # Should output: local
  ```
- [ ] Backend builds successfully:
  ```bash
  cd backend
  mvn clean package  # Should complete without errors
  ```
- [ ] WAR file created at `backend/target/bbj-church-manager.war`
- [ ] Configuration files exist:
  - [ ] `backend/src/config-local.properties` ✓
  - [ ] `backend/src/config-production.properties` ✓
- [ ] ConfigManager can read properties
- [ ] DBConnection tests locally:
  ```bash
  curl -X POST http://localhost:8080/bbj/api/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@test.com","password":"test"}'
  # Should return JSON (error OK if user doesn't exist)
  ```

### Frontend Setup
- [ ] Node.js installed (v16+):
  ```bash
  node -v  # Should be v16 or higher
  ```
- [ ] npm installed:
  ```bash
  npm -v  # Should be 8 or higher
  ```
- [ ] Dependencies installed:
  ```bash
  cd frontend
  npm install  # Should complete without errors
  ```
- [ ] .env.local file created
- [ ] React dev server starts:
  ```bash
  npm start  # Should open http://localhost:3000
  ```

---

## Phase 2: Local Development Testing

### 2.1 Backend API Endpoints

#### Authentication
- [ ] **POST /api/login** - Login endpoint
  - [ ] Valid credentials return user data
  - [ ] Invalid credentials return 401
  - [ ] Response includes userId, userType, name
  - [ ] Test both member and admin login
  
  ```bash
  curl -X POST http://localhost:8080/bbj/api/login \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@bbj.com","password":"admin"}'
  ```

- [ ] **POST /api/register** - Registration endpoint
  - [ ] New member registration succeeds
  - [ ] Duplicate email returns error
  - [ ] Response includes auto-generated email
  - [ ] Member appears in database
  
  ```bash
  curl -X POST http://localhost:8080/bbj/api/register \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Member"}'
  ```

#### Member Management (Admin Only)
- [ ] **GET /api/members** - List all members
  - [ ] Returns array of members
  - [ ] Includes: id, name, email, status, joined_date
  - [ ] Requires authentication (optional for this demo)

- [ ] **GET /api/members/{id}** - Get single member
  - [ ] Returns specific member data
  - [ ] Returns 404 if not found

- [ ] **PUT /api/members** - Update member
  - [ ] Updates existing member record
  - [ ] Changes persist in database
  - [ ] Invalid ID returns error

- [ ] **DELETE /api/members/{id}** - Delete member
  - [ ] Removes member from database
  - [ ] Subsequent GET returns 404
  - [ ] Invalid ID returns error

#### Announcements
- [ ] **GET /api/announcements** - List announcements
  - [ ] Returns all announcements in reverse date order
  - [ ] Response format includes: id, title, message, created_by, created_date

- [ ] **POST /api/announcements** - Create announcement
  - [ ] Admin can create new announcement
  - [ ] Stores to database
  - [ ] Returns created announcement with ID
  
  ```bash
  curl -X POST http://localhost:8080/bbj/api/announcements \
    -H "Content-Type: application/json" \
    -d '{"title":"Test Announcement","message":"This is a test","createdBy":1}'
  ```

- [ ] **PUT /api/announcements** - Update announcement
  - [ ] Admin can edit existing announcement
  - [ ] Changes persist

- [ ] **DELETE /api/announcements/{id}** - Delete announcement
  - [ ] Admin can remove announcement
  - [ ] Deleted announcement no longer appears in list

#### Events
- [ ] **GET /api/events** - List all events
  - [ ] Returns events with: id, title, description, event_date, location

- [ ] **GET /api/events?upcoming=true** - List upcoming events
  - [ ] Returns only events with event_date > now
  - [ ] Sorted by event_date ascending

- [ ] **POST /api/events** - Create event
  - [ ] Admin can create with: title, description, event_date, location
  - [ ] Event date must be valid DateTime format
  - [ ] Event persists in database

- [ ] **PUT /api/events** - Update event
  - [ ] Admin can edit event details
  - [ ] Changes persist

- [ ] **DELETE /api/events/{id}** - Delete event
  - [ ] Event removed from database

#### Sermons
- [ ] **GET /api/sermons** - List sermons
  - [ ] Returns: id, title, description, file_path, file_type, uploaded_date

- [ ] **POST /api/sermons/upload** - Upload sermon with file
  - [ ] Accepts multipart/form-data with file
  - [ ] File types supported: MP3, MP4
  - [ ] File uploads to Backblaze B2
  - [ ] Returns JSON with file URL
  - [ ] Sermon metadata stored in database
  
  ```bash
  curl -X POST http://localhost:8080/bbj/api/sermons/upload \
    -F "title=Test Sermon" \
    -F "description=A test sermon file" \
    -F "uploadedBy=1" \
    -F "file=@/path/to/sermon.mp3"
  ```

- [ ] **PUT /api/sermons** - Update sermon metadata
  - [ ] Can update title/description
  - [ ] File_path cannot be changed
  
- [ ] **DELETE /api/sermons/{id}** - Delete sermon
  - [ ] Removes from database
  - [ ] Optionally deletes from B2

### 2.2 Frontend Components

#### Navigation (Navbar)
- [ ] **Desktop View** (width > 768px)
  - [ ] Logo/site name visible on left
  - [ ] Menu items: Home, Announcements, Events, Sermons visible
  - [ ] Login/Register visible on right
  - [ ] Dropdown menu for logged-in user shows: My Profile, Logout
  - [ ] Admin users see: Admin Dashboard option

- [ ] **Mobile View** (width < 768px)
  - [ ] Hamburger menu icon visible
  - [ ] Clicking hamburger shows/hides menu
  - [ ] Menu items stack vertically
  - [ ] Login/Register accessible from mobile menu

#### Pages - Responsive Design
- [ ] All pages load without console errors
- [ ] All pages work on: Mobile (375px), Tablet (768px), Desktop (1024px+)
- [ ] Text is readable, not cut off on any device size
- [ ] Images scale appropriately
- [ ] Buttons are touch-friendly on mobile (min 44x44px)

#### Home Page
- [ ] Hero section visible with lemon yellow background
- [ ] "Welcome to BBJ Church" heading visible
- [ ] "Latest Announcements" section shows recent announcements
  - [ ] Shows max 3 announcements
  - [ ] Truncates long text
  - [ ] "View All" button links to announcements page
  
- [ ] "Featured Events" section shows upcoming events
  - [ ] Shows max 3 events
  - [ ] Displays: event title, date, time, location
  - [ ] "View All" button works
  
- [ ] "Latest Sermons" section shows recent sermons
  - [ ] Shows max 3 sermons
  - [ ] Play/Watch buttons present
  - [ ] "View All" button works

- [ ] Feature cards present: Announcements, Events, Sermons, Admin Portal
  - [ ] Hover effects work (deep teal background)
  - [ ] Cards link to respective pages

#### Login Page
- [ ] Role selector visible: "Member" and "Admin" radio buttons
- [ ] Email input field accepts valid emails
- [ ] Password input field hides text
- [ ] Login button submits form
- [ ] Valid credentials:
  - [ ] Store userId, userType, userName in localStorage
  - [ ] Redirect to home page
  
- [ ] Invalid credentials:
  - [ ] Display error message
  - [ ] Don't redirect
  - [ ] Allow retry
  
- [ ] "Don't have an account?" link goes to Register page

#### Register Page
- [ ] Full Name input field visible and required
- [ ] Password input field visible and hidden
- [ ] Confirm Password input field visible and hidden
- [ ] Password mismatch shows error
- [ ] Successfully register:
  - [ ] Auto-generated email displays
  - [ ] "Copy to Clipboard" button works
  - [ ] Success message shows
  - [ ] Redirects to login after 2 seconds
  
- [ ] Member appears in Members list (admin view)

#### Announcements Page
- [ ] Lists all announcements in reverse date order (newest first)
- [ ] Each announcement shows:
  - [ ] Title
  - [ ] Message (full text or truncated)
  - [ ] Created date
  - [ ] Created by (admin name)
  
- [ ] Admin users see edit/delete buttons
- [ ] Delete confirmation dialog appears
- [ ] Updates reflect immediately after admin action
- [ ] Scrolling shows more announcements (pagination or infinite scroll)

#### Events Page
- [ ] Lists all upcoming events (event_date > current date)
- [ ] Events sorted by event_date (nearest first)
- [ ] Each event card shows:
  - [ ] Title
  - [ ] Description
  - [ ] Date and time (formatted: "Jan 15, 2024 at 6:00 PM")
  - [ ] Location
  
- [ ] Calendar/event icon visible
- [ ] Admin users see:
  - [ ] Create Event button
  - [ ] Edit/delete buttons on each event
  - [ ] Modal form for creating/editing events
  
- [ ] Modal form includes:
  - [ ] Title input
  - [ ] Description textarea
  - [ ] Date picker
  - [ ] Time picker
  - [ ] Location input
  - [ ] Save and Cancel buttons

#### Sermons Page
- [ ] Lists all sermons
- [ ] Each sermon shows:
  - [ ] Title
  - [ ] Description
  - [ ] Upload date
  - [ ] File type badge (MP3 or MP4)
  - [ ] Play/Download button (for audio)
  - [ ] Watch/Download button (for video)
  
- [ ] Audio player works:
  - [ ] Play, pause, seek controls visible
  - [ ] Volume control present
  - [ ] Duration displays correctly
  
- [ ] Video player works:
  - [ ] Play, pause controls visible
  - [ ] Full screen button available
  - [ ] Volume control present
  - [ ] Duration displays correctly
  
- [ ] Admin users see:
  - [ ] Upload Sermon button
  - [ ] Edit/delete buttons on each sermon
  - [ ] Modal form for uploading sermons

#### Admin Dashboard
- [ ] Only accessible after admin login
- [ ] Tabbed interface with 4 tabs:
  - [ ] Members
  - [ ] Announcements
  - [ ] Events
  - [ ] Sermons

- [ ] **Members Tab**
  - [ ] Lists all members in table/grid format
  - [ ] Shows: Name, Email, Status, Joined Date
  - [ ] Edit button opens member form
  - [ ] Delete button with confirmation
  - [ ] Changes save to database

- [ ] **Announcements Tab**
  - [ ] List of announcements with create button
  - [ ] Create form has: Title, Message fields
  - [ ] Edit/delete functionality works
  - [ ] Changes persist

- [ ] **Events Tab**
  - [ ] List of all events (including past events)
  - [ ] Create button opens modal
  - [ ] Event form has all required fields
  - [ ] Edit/delete functionality works

- [ ] **Sermons Tab**
  - [ ] List of all sermons
  - [ ] Upload button opens file dialog
  - [ ] File upload form has:
    - [ ] Title input
    - [ ] Description textarea
    - [ ] File input (accepts .mp3, .mp4 only)
    - [ ] Upload button
    - [ ] Progress indicator during upload
  
  - [ ] Successful upload:
    - [ ] File appears in B2 bucket
    - [ ] Sermon metadata saved to database
    - [ ] New sermon appears in list
    - [ ] Success notification shows
  
  - [ ] Failed upload:
    - [ ] Error message displays
    - [ ] Allows retry

### 2.3 User Sessions
- [ ] **Login Persistence**
  - [ ] Refresh page while logged in
  - [ ] User remains logged in (data from localStorage)
  - [ ] Can navigate between pages without logging in again
  
- [ ] **Logout**
  - [ ] Logout button in navbar works
  - [ ] Clears localStorage
  - [ ] Redirects to home page
  - [ ] Cannot access protected pages (admin dashboard)
  
- [ ] **Protected Routes**
  - [ ] Accessing admin dashboard while not logged in:
    - [ ] Redirects to login page
    - [ ] Shows message "Please log in as admin"
  
  - [ ] Accessing admin dashboard as member:
    - [ ] Redirects to home or shows access denied message

### 2.4 Backblaze B2 Integration
- [ ] B2 connection test succeeds:
  ```bash
  curl -X GET http://localhost:8080/bbj/api/b2-test
  # Should return {"connected":true}
  ```

- [ ] File upload to B2:
  - [ ] Upload sermon via admin dashboard
  - [ ] Check B2 web console → bbj-church-media bucket
  - [ ] File appears with timestamp prefix (e.g., "1704067200_sermon.mp3")
  - [ ] File metadata correct (size, upload date)
  
- [ ] File download from B2:
  - [ ] Click play on sermon
  - [ ] File streams from B2 (check Network tab in DevTools)
  - [ ] Audio/video plays without buffering issues
  
- [ ] File deletion from B2:
  - [ ] Delete sermon from admin dashboard
  - [ ] Confirm file removed from B2 bucket

### 2.5 Data Persistence
- [ ] **Database Queries After Restart**
  - [ ] Restart Tomcat
  - [ ] Registered members still exist
  - [ ] Announcements/events/sermons still exist
  - [ ] Admin can edit existing records
  
  ```bash
  mysql -u root bbj -e "SELECT COUNT(*) FROM members;"
  # Should show members count
  ```

- [ ] **Session Recovery**
  - [ ] After database restart
  - [ ] Can login with original credentials
  - [ ] Data integrity preserved

---

## Phase 3: Production Testing (TiDB Cloud)

### Pre-Production Verification
- [ ] TiDB Cloud account active and accessible
- [ ] Cluster running (check TiDB Cloud console)
- [ ] Connection test succeeds:
  ```bash
  mysql -h gateway01.eu-central-1.prod.aws.tidbcloud.com \
        -P 4000 \
        -u "nrMPj1ECajN3NtY.root" \
        -p
  # Enter password: Gt19N5jWU7BMaDl5
  # Should connect successfully
  ```

- [ ] Database and tables exist:
  ```sql
  USE bbj;
  SHOW TABLES;
  # Should show: admins, announcements, events, members, sermons
  ```

### Production Deployment
- [ ] ENVIRONMENT variable set to `production` on server
- [ ] Backend built and deployed to production Tomcat
- [ ] Frontend built and deployed (Nginx or Tomcat)
- [ ] HTTPS certificate installed and valid
- [ ] Configuration files have production values
- [ ] Backups configured (automated for TiDB Cloud)

### Production API Testing
- [ ] All endpoints reachable via HTTPS:
  ```bash
  curl -k https://your-domain.com/api/members
  # Should return JSON
  ```

- [ ] TiDB Cloud database reads work
- [ ] TiDB Cloud database writes work
- [ ] File uploads go to B2 (same credentials)
- [ ] No connection errors in logs

### Production UI Testing
- [ ] Frontend loads without console errors
- [ ] API calls from frontend succeed
- [ ] Login/register work with TiDB Cloud
- [ ] File upload works end-to-end
- [ ] File playback works (downloads from B2)
- [ ] Admin dashboard fully functional

### Performance & Load Testing
- [ ] Login response time < 1 second
- [ ] Member list loads < 2 seconds
- [ ] File upload progress accurate
- [ ] File playback smooth (no buffering)
- [ ] 10 simultaneous connections don't cause errors
- [ ] Memory usage stable (no leaks)

---

## Phase 4: Security Testing

### Authentication & Authorization
- [ ] Cannot access member endpoints without login
- [ ] Cannot access admin endpoints as regular member
- [ ] JWTs/session tokens expire correctly
- [ ] Cannot modify other user's data
- [ ] Password hashed in database (not plain text)

### Input Validation
- [ ] SQL injection attempts fail gracefully:
  ```bash
  curl -d '{"email":"admin\" OR \"1\"=\"1","password":"x"}' ...
  # Should fail, not grant access
  ```

- [ ] XSS attempts blocked:
  - [ ] Announcement with `<script>alert('xss')</script>` 
  - [ ] Script doesn't execute on page load
  - [ ] Text displayed as-is, not executed
  
- [ ] File upload restrictions:
  - [ ] Cannot upload non-MP3/MP4 files
  - [ ] Cannot upload 1GB file (500MB max)
  - [ ] Cannot upload executable files

### Data Protection
- [ ] HTTPS enforced (HTTP redirects to HTTPS)
- [ ] B2 credentials not exposed in:
  - [ ] Frontend code
  - [ ] Browser Network tab
  - [ ] API responses
  - [ ] Client localStorage

---

## Phase 5: Browser Compatibility

### Desktop Browsers
- [ ] **Chrome** (latest)
  - [ ] All pages load
  - [ ] No console errors
  - [ ] All features work
  
- [ ] **Firefox** (latest)
  - [ ] All pages load
  - [ ] No console errors
  - [ ] All features work
  
- [ ] **Safari** (latest)
  - [ ] All pages load
  - [ ] All features work
  
- [ ] **Edge** (latest)
  - [ ] All pages load
  - [ ] All features work

### Mobile Browsers
- [ ] **Chrome Mobile** (iOS/Android)
  - [ ] Responsive layout works
  - [ ] Touch interactions work
  - [ ] File upload works
  
- [ ] **Safari Mobile** (iOS)
  - [ ] Responsive layout works
  - [ ] Video playback works

### Tablets
- [ ] **iPad** (all sizes)
  - [ ] Content readable
  - [ ] Touch interactions responsive
  
- [ ] **Android Tablet**
  - [ ] Content readable
  - [ ] All features work

---

## Test Results Tracking

### Local Development Results
```
Date: ________________
Tester: ________________

PASSED:
- [ ] All backend endpoints
- [ ] All frontend pages
- [ ] B2 file upload
- [ ] User authentication
- [ ] Data persistence

FAILED:
(List any failures with details)

NOTES:
(Any observations or issues)
```

### Production Results
```
Date: ________________
Tester: ________________
Environment: TiDB Cloud

PASSED:
- [ ] TiDB Cloud connection
- [ ] All API endpoints
- [ ] File upload/download
- [ ] User data integrity
- [ ] Performance acceptable

FAILED:
(List any failures with details)

NOTES:
(Any observations or issues)
```

---

## Sign-Off

- [ ] All priority tests passed
- [ ] No critical bugs found
- [ ] Performance acceptable
- [ ] Security requirements met
- [ ] Ready for production

**Developer**: ________________  
**Date**: ________________  
**Sign-off**: ________________

