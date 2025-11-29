# 🚀 Complete Render Deployment Guide

## Step 1: Prepare Your Code

### 1.1 Build the JAR file
```bash
cd Backend
./mvnw clean package -DskipTests
```

### 1.2 Test locally (optional)
```bash
java -jar target/yaAllah-0.0.1-SNAPSHOT.jar
```

## Step 2: Git Repository Setup

### 2.1 Initialize Git (if not done)
```bash
git init
git add .
git commit -m "Initial commit"
```

### 2.2 Create GitHub Repository
1. Go to GitHub.com
2. Click "New Repository"
3. Name: `yaallah-backend`
4. Make it Public
5. Don't initialize with README

### 2.3 Push to GitHub
```bash
git remote add origin https://github.com/YOUR_USERNAME/yaallah-backend.git
git branch -M main
git push -u origin main
```

## Step 3: Render Deployment

### 3.1 Create Render Account
1. Go to render.com
2. Sign up with GitHub

### 3.2 Create PostgreSQL Database
1. Click "New +"
2. Select "PostgreSQL"
3. Name: `yaallah-db`
4. Database Name: `springboot_db_hz7s`
5. User: `springboot_db_hz7s_user`
6. Region: Oregon (US West)
7. Plan: Free
8. Click "Create Database"

### 3.3 Get Database Credentials
After database is created, copy:
- **External Database URL** (starts with postgresql://)
- **Username**
- **Password**

### 3.4 Create Web Service
1. Click "New +"
2. Select "Web Service"
3. Connect your GitHub repository
4. Select `yaallah-backend` repo
5. Configure:
   - **Name**: `yaallah-backend`
   - **Region**: Oregon (US West)
   - **Branch**: main
   - **Runtime**: Docker
   - **Plan**: Free

### 3.5 Set Environment Variables
In the Environment section, add:

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://username:password@host:port/database?sslmode=require
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://allah-blog.vercel.app
```

Replace the DATABASE_URL with your actual database URL from step 3.3

### 3.6 Deploy
1. Click "Create Web Service"
2. Wait for deployment (5-10 minutes)
3. Your API will be available at: `https://yaallah-backend.onrender.com`

## Step 4: Update Frontend

Update your frontend API base URL to:
```javascript
const API_BASE_URL = 'https://yaallah-backend.onrender.com';
```

## Step 5: Test Deployment

### 5.1 Health Check
Visit: `https://yaallah-backend.onrender.com/actuator/health`

### 5.2 API Documentation
Visit: `https://yaallah-backend.onrender.com/swagger-ui.html`

### 5.3 Test Endpoints
```bash
curl https://yaallah-backend.onrender.com/api/posts
```

## 🔧 Troubleshooting

### Database Connection Issues
- Check DATABASE_URL format
- Ensure sslmode=require is included
- Verify credentials are correct

### Build Failures
- Check Dockerfile syntax
- Ensure all dependencies are in pom.xml
- Check Java version compatibility

### CORS Issues
- Update CORS_ALLOWED_ORIGINS with your frontend URL
- Add your domain to the environment variable

## 📝 Important Notes

1. **Free Tier Limitations**:
   - Service sleeps after 15 minutes of inactivity
   - First request after sleep takes 30+ seconds
   - 750 hours/month limit

2. **Database**:
   - Free PostgreSQL has 1GB storage limit
   - Automatically backed up

3. **Logs**:
   - Check Render dashboard for deployment logs
   - Use "Events" tab for real-time monitoring

## 🎯 Quick Commands

```bash
# Build JAR
./mvnw clean package -DskipTests

# Git commands
git add .
git commit -m "Deploy to render"
git push origin main

# Test locally with prod profile
java -Dspring.profiles.active=prod -jar target/yaAllah-0.0.1-SNAPSHOT.jar
```

Your backend will be live at: **https://yaallah-backend.onrender.com** 🎉