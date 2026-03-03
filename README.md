# BBJ Church Manager - Web-Based Church Management System

A modern, full-stack web application for managing church operations including members, announcements, events, and sermon distribution.

## 🎨 Features

### Member Features
- ✅ User Registration with auto-generated email
- ✅ Secure Login/Authentication
- ✅ View Announcements (READ-ONLY)
- ✅ View Events Calendar
- ✅ Download Sermons (MP4 & MP3)
- ✅ Responsive Mobile-First Design

### Admin Features
- ✅ Member Management (Add, View, Delete)
- ✅ Create & Manage Announcements
- ✅ Create & Manage Events
- ✅ Upload Sermons (MP3 & MP4)
- ✅ Comprehensive Dashboard

### Design
- 🎯 **Theme Colors**: Lemon Yellow (#F4D03F) & Deep Teal (#0F766E)
- 📱 **Responsive Design**: Mobile, Tablet, and Desktop support
- 🎨 **Modern UI**: Built with Tailwind CSS
- ⚡ **Fast & Efficient**: Optimized React components

## 🛠️ Technology Stack

### Backend
- **Language**: Java
- **Framework**: Servlets & JSP
- **Database**: MySQL
- **Build Tool**: Maven/Gradle

### Frontend
- **Framework**: React 18
- **Styling**: Tailwind CSS 3
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Package Manager**: npm/yarn

### Database
- **DBMS**: MySQL 8+
- **Schema**: Fully normalized with proper relationships

## 📋 Prerequisites

### Backend
- Java Development Kit (JDK) 11+
- Apache Tomcat 9+
- Maven 3.6+
- MySQL Server 8+

### Frontend
- Node.js 16+
- npm 8+ or yarn 1.22+

## 🚀 Installation & Setup

### 1. Database Setup

```bash
# Create database and tables
mysql -u root -p < database/bbj.sql
```

### 2. Backend Setup

```bash
cd backend

# Ensure MySQL is running on localhost:1532
# Update DBConnection.java if your MySQL port/credentials differ

# Build the project
mvn clean install

# Deploy to Tomcat
# Copy target/bbj.war to Tomcat webapps folder
# OR run with embedded server (if available)
```

**Update DBConnection.java if needed:**
```java
DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/bbj",  // Change port if needed
    "root",                             // MySQL username
    "your_password"                     // MySQL password
);
```

### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Update API URL if backend is on different host
# Edit src/services/api.js if needed
export const API_URL = 'http://localhost:8080/api';

# Start development server
npm start

# Build for production
npm run build
```

## 📝 API Endpoints

### Authentication
- `POST /api/login` - Login (member/admin)
- `POST /api/register` - Register new member

### Members (Admin Only)
- `GET /api/members` - Get all members
- `GET /api/members/{id}` - Get specific member
- `PUT /api/members` - Update member
- `DELETE /api/members/{id}` - Delete member

### Announcements
- `GET /api/announcements` - Get all announcements
- `GET /api/announcements/{id}` - Get specific announcement
- `POST /api/announcements` - Create announcement (Admin)
- `PUT /api/announcements` - Update announcement (Admin)
- `DELETE /api/announcements/{id}` - Delete announcement (Admin)

### Events
- `GET /api/events` - Get all events
- `GET /api/events?upcoming=true` - Get upcoming events only
- `GET /api/events/{id}` - Get specific event
- `POST /api/events` - Create event (Admin)
- `PUT /api/events` - Update event (Admin)
- `DELETE /api/events/{id}` - Delete event (Admin)

### Sermons
- `GET /api/sermons` - Get all sermons
- `GET /api/sermons/{id}` - Get specific sermon
- `POST /api/sermons` - Upload sermon (Admin)
- `PUT /api/sermons` - Update sermon (Admin)
- `DELETE /api/sermons/{id}` - Delete sermon (Admin)

## 📁 Project Structure

```
BBJ digital/
├── backend/
│   ├── src/
│   │   ├── dao/          # Data Access Objects
│   │   │   ├── MemberDAO.java
│   │   │   ├── AdminDAO.java
│   │   │   ├── SermonDAO.java
│   │   │   ├── AnnouncementDAO.java
│   │   │   └── EventDAO.java
│   │   ├── db/           # Database Connection
│   │   │   └── DBConnection.java
│   │   ├── model/        # Entity Classes
│   │   │   ├── Member.java
│   │   │   ├── Admin.java
│   │   │   ├── Sermon.java
│   │   │   ├── Announcement.java
│   │   │   └── Event.java
│   │   └── servlet/      # HTTP Endpoints
│   │       ├── LoginServlet.java
│   │       ├── RegisterServlet.java
│   │       ├── MemberServlet.java
│   │       ├── AnnouncementServlet.java
│   │       ├── EventServlet.java
│   │       └── SermonServlet.java
│   └── pom.xml          # Maven Configuration
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   └── Navbar.jsx
│   │   ├── layouts/
│   │   │   └── Layout.jsx
│   │   ├── pages/
│   │   │   ├── Home.jsx
│   │   │   ├── Login.jsx
│   │   │   ├── Register.jsx
│   │   │   ├── Announcements.jsx
│   │   │   ├── Events.jsx
│   │   │   ├── Sermons.jsx
│   │   │   └── AdminDashboard.jsx
│   │   ├── services/
│   │   │   └── api.js
│   │   ├── App.jsx
│   │   ├── index.jsx
│   │   └── index.css
│   ├── public/
│   │   └── index.html
│   ├── package.json
│   ├── tailwind.config.js
│   └── postcss.config.js
│
└── database/
    └── bbj.sql        # Database Schema
```

## 🔑 Key Models

### Member
```java
{
  id: int,
  name: string,
  email: string (auto-generated),
  password: string (hashed),
  status: 'active' | 'inactive',
  joinedDate: timestamp,
  updatedAt: timestamp
}
```

### Admin
```java
{
  id: int,
  name: string,
  email: string,
  password: string (hashed),
  createdBy: int (admin id),
  createdDate: timestamp,
  updatedAt: timestamp
}
```

### Sermon
```java
{
  id: int,
  title: string,
  description: string,
  filePath: string (URL),
  fileType: 'mp3' | 'mp4',
  uploadedBy: int (admin id),
  uploadedDate: timestamp,
  updatedAt: timestamp
}
```

### Announcement
```java
{
  id: int,
  title: string,
  message: string (fulltext),
  createdBy: int (admin id),
  createdDate: timestamp,
  updatedAt: timestamp
}
```

### Event
```java
{
  id: int,
  title: string,
  description: string,
  eventDate: datetime,
  location: string,
  createdBy: int (admin id),
  createdDate: timestamp,
  updatedAt: timestamp
}
```

## 🎯 User Flow

### Member Journey
1. **Landing Page** → View featured content
2. **Register** → Auto-generated email provided
3. **Login** → Access member portal
4. **Browse** → Announcements, Events, Sermons
5. **Download** → Sermon files available

### Admin Journey
1. **Login** → Admin credentials
2. **Dashboard** → Comprehensive management interface
3. **Manage** → Members, Announcements, Events, Sermons
4. **Upload** → Sermon files (MP3/MP4)
5. **Monitor** → User activity and content

## 🔒 Security Features

- ✅ Password hashing
- ✅ SQL injection prevention (Prepared Statements)
- ✅ CORS headers enabled
- ✅ Role-based access control (Member/Admin)
- ✅ Session management via localStorage
- ✅ Input validation on both frontend and backend

## 📱 Responsive Breakpoints

- **Mobile**: < 768px
- **Tablet**: 768px - 1024px
- **Desktop**: > 1024px

## 🎨 Color Scheme

- **Primary**: Deep Teal (#0F766E) - Professional background
- **Accent**: Lemon Yellow (#F4D03F) - Highlights and buttons
- **Background**: White (#FFFFFF) - Clean content areas
- **Text**: Dark Teal (#0F766E) for headings, Gray for body text

## 🚀 Deployment

### Backend Deployment
```bash
# Build WAR file
mvn clean package

# Deploy to Tomcat
cp target/bbj.war $TOMCAT_HOME/webapps/
```

### Frontend Deployment
```bash
# Build production version
npm run build

# Deploy dist folder to web server
# (Nginx, Apache, or any static file server)
```

## 🐛 Troubleshooting

### MySQL Connection Error
- Ensure MySQL is running
- Check port number (default: 3306)
- Verify credentials in DBConnection.java

### CORS Errors
- Verify backend API URL in frontend/src/services/api.js
- Check if backend CORS headers are set correctly

### React Module Not Found
```bash
npm install
```

### Port Already in Use
```bash
# Change port in React package.json:
"start": "PORT=3001 react-scripts start"
```

## 📞 Support & Contributions

This project was built as a semester project for church management. For issues or improvements, please refer to the code comments and documentation.

## 📄 License

© 2026 BBJ Church. All rights reserved.

---

**Happy church managing! 🙏⛪**
#   b b j 2  
 