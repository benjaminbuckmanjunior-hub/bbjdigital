# BBJ Church Manager - Local Build & Deploy Script
# This script builds the backend and frontend, then deploys to Tomcat for local testing

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# ============================================================================
# Configuration
# ============================================================================
$SCRIPT_DIR = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition
$WORKSPACE = "C:\Users\Buckman\Desktop\BBJ digital"
$MAVEN_HOME = "C:\apache-maven-3.9.12"
$TOMCAT_HOME = "C:\apache-tomcat-9.0"
$NODEJS_HOME = "C:\Program Files\nodejs"

# Try to auto-detect JAVA_HOME from common locations
$JAVA_HOME = $null
$JAVA_SEARCH_PATHS = @(
    "C:\Program Files\Java\jdk-11*",
    "C:\Program Files\Java\jdk-17*",
    "C:\Program Files\Java\jdk-21*",
    "C:\Program Files (x86)\Java\jdk-11*",
    "$env:JAVA_HOME"
) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1

if ($JAVA_SEARCH_PATHS) {
    $JAVA_HOME = Resolve-Path $JAVA_SEARCH_PATHS | Select-Object -ExpandProperty Path
}

$BACKEND_DIR = "$WORKSPACE\backend"
$FRONTEND_DIR = "$WORKSPACE\frontend"
$TOMCAT_WEBAPPS = "$TOMCAT_HOME\webapps"

# ============================================================================
# Helper Functions
# ============================================================================
function Write-Header($text) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host $text -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
}

function Write-Status($text, $status = "INFO") {
    $color = switch ($status) {
        "SUCCESS" { "Green" }
        "ERROR" { "Red" }
        "WARNING" { "Yellow" }
        default { "Cyan" }
    }
    Write-Host "[$status] $text" -ForegroundColor $color
}

function Exit-WithError($message) {
    Write-Status $message "ERROR"
    exit 1
}

function Check-Command($command) {
    $exists = $null -ne (Get-Command $command -ErrorAction SilentlyContinue)
    if (-not $exists) {
        Exit-WithError "Command not found: $command"
    }
}

# ============================================================================
# Verify Prerequisites
# ============================================================================
Write-Header "Checking Prerequisites"

if (-not (Test-Path $MAVEN_HOME)) {
    Exit-WithError "Maven not found at: $MAVEN_HOME"
}
Write-Status "Maven found at: $MAVEN_HOME" "SUCCESS"

if (-not $JAVA_HOME -or -not (Test-Path $JAVA_HOME)) {
    Exit-WithError "JAVA_HOME not found. Please install JDK 11+ (C:\Program Files\Java\jdk*)"
}
Write-Status "JAVA_HOME found at: $JAVA_HOME" "SUCCESS"

if (-not (Test-Path $TOMCAT_HOME)) {
    Exit-WithError "Tomcat not found at: $TOMCAT_HOME"
}
Write-Status "Tomcat found at: $TOMCAT_HOME" "SUCCESS"

if (-not (Test-Path $NODEJS_HOME)) {
    Exit-WithError "Node.js not found at: $NODEJS_HOME"
}
Write-Status "Node.js found at: $NODEJS_HOME" "SUCCESS"

if (-not (Test-Path $BACKEND_DIR)) {
    Exit-WithError "Backend directory not found at: $BACKEND_DIR"
}
Write-Status "Backend directory found" "SUCCESS"

if (-not (Test-Path $FRONTEND_DIR)) {
    Exit-WithError "Frontend directory not found at: $FRONTEND_DIR"
}
Write-Status "Frontend directory found" "SUCCESS"

# ============================================================================
# Set Environment Variables
# ============================================================================
Write-Header "Setting Environment Variables"

$env:MAVEN_HOME = $MAVEN_HOME
$env:JAVA_HOME = $JAVA_HOME  # Actual JDK installation
$env:PATH = "$JAVA_HOME\bin;$MAVEN_HOME\bin;$NODEJS_HOME;$env:PATH"
$env:ENVIRONMENT = "local"

Write-Status "JAVA_HOME = $env:JAVA_HOME" "SUCCESS"
Write-Status "MAVEN_HOME = $env:MAVEN_HOME" "SUCCESS"
Write-Status "ENVIRONMENT = $env:ENVIRONMENT" "SUCCESS"
Write-Status "Node.js in PATH" "SUCCESS"

# ============================================================================
# Stop Tomcat if Running
# ============================================================================
Write-Header "Stopping Tomcat (if running)"

try {
    # Try to find Tomcat process by checking executable path
    $tomcatProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue | 
                     Where-Object { $_.Path -like "*bin*" } | 
                     Select-Object -First 1
    
    # Alternative: look for bootstrap.jar in the command line
    if (-not $tomcatProcess) {
        $tomcatProcess = Get-WmiObject Win32_Process -Filter "name = 'java.exe'" -ErrorAction SilentlyContinue |
                        Where-Object { $_.CommandLine -like "*catalina*" -or $_.CommandLine -like "*tomcat*" } |
                        Select-Object -First 1
    }
    
    if ($tomcatProcess) {
        Write-Status "Stopping Tomcat..." "INFO"
        if ($tomcatProcess.ProcessId) {
            Stop-Process -Id $tomcatProcess.ProcessId -Force -ErrorAction SilentlyContinue
        } else {
            Stop-Process -InputObject $tomcatProcess -Force -ErrorAction SilentlyContinue
        }
        Start-Sleep -Seconds 3
        Write-Status "Tomcat stopped" "SUCCESS"
    } else {
        Write-Status "Tomcat not running" "INFO"
    }
} catch {
    Write-Status "Could not stop Tomcat: $_" "WARNING"
}

# ============================================================================
# Fix Java File Encoding (UTF-16 BOM to UTF-8)
# ============================================================================
Write-Header "Fixing Java File Encoding Issues"

$fixEncodingScript = Join-Path $SCRIPT_DIR "FixEncoding.ps1"
if (Test-Path $fixEncodingScript) {
    try {
        & $fixEncodingScript -SourceDir (Join-Path $BACKEND_DIR "src")
        Write-Status "Java file encoding fixed" "SUCCESS"
    } catch {
        Write-Status "Warning: Could not fix encoding: $_" "WARNING"
    }
} else {
    Write-Status "FixEncoding.ps1 not found at $fixEncodingScript" "WARNING"
}

# ============================================================================
# Clean Backend
# ============================================================================
Write-Header "Cleaning Backend"

Push-Location $BACKEND_DIR
try {
    Write-Status "Running: mvn clean" "INFO"
    & mvn clean
    if ($LASTEXITCODE -ne 0) {
        Exit-WithError "Maven clean failed with exit code: $LASTEXITCODE"
    }
    Write-Status "Backend cleaned" "SUCCESS"
} finally {
    Pop-Location
}

# ============================================================================
# Build Backend
# ============================================================================
Write-Header "Building Backend with Maven"

Push-Location $BACKEND_DIR
try {
    Write-Status "Running: mvn package -DskipTests" "INFO"
    & mvn package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Exit-WithError "Maven build failed with exit code: $LASTEXITCODE"
    }
    Write-Status "Backend built successfully" "SUCCESS"
} finally {
    Pop-Location
}

# ============================================================================
# Deploy Backend WAR to Tomcat
# ============================================================================
Write-Header "Deploying Backend to Tomcat"

$WAR_FILE = "$BACKEND_DIR\target\bbj-church-manager.war"
$TARGET_WAR = "$TOMCAT_WEBAPPS\api.war"

if (-not (Test-Path $WAR_FILE)) {
    Exit-WithError "WAR file not found at: $WAR_FILE"
}

# Remove old ROOT application and old api application
Get-ChildItem $TOMCAT_WEBAPPS -Include "api*" -Force | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
Write-Status "Cleaned old Tomcat deployment" "INFO"

# Copy WAR file
Copy-Item $WAR_FILE -Destination $TARGET_WAR -Force
Write-Status "Deployed to: $TARGET_WAR" "SUCCESS"

# ============================================================================
# Build Frontend
# ============================================================================
Write-Header "Building Frontend with npm"

Push-Location $FRONTEND_DIR
try {
    Write-Status "Running: npm install" "INFO"
    & npm install
    if ($LASTEXITCODE -ne 0) {
        Exit-WithError "npm install failed with exit code: $LASTEXITCODE"
    }
    Write-Status "Dependencies installed" "SUCCESS"

    Write-Status "Running: npm run build" "INFO"
    & npm run build
    if ($LASTEXITCODE -ne 0) {
        Exit-WithError "npm build failed with exit code: $LASTEXITCODE"
    }
    Write-Status "Frontend built successfully" "SUCCESS"
} finally {
    Pop-Location
}

# ============================================================================
# Set Database Environment for Tomcat
# ============================================================================
Write-Header "Setting Tomcat Environment for Local Database"

$CATALINA_OPTS = "-DENVIRONMENT=local"
$ENVIRONMENT_FILE = "$TOMCAT_HOME\bin\setenv.bat"

@"
@echo off
set ENVIRONMENT=local
"@ | Out-File -FilePath $ENVIRONMENT_FILE -Encoding ASCII

Write-Status "Tomcat environment configured for: local" "SUCCESS"

# ============================================================================
# Start Tomcat
# ============================================================================
Write-Header "Starting Tomcat"

$STARTUP_SCRIPT = "$TOMCAT_HOME\bin\startup.bat"

if (-not (Test-Path $STARTUP_SCRIPT)) {
    Exit-WithError "Tomcat startup script not found at: $STARTUP_SCRIPT"
}

Write-Status "Launching Tomcat..." "INFO"
& cmd.exe /c $STARTUP_SCRIPT
Start-Sleep -Seconds 5

Write-Status "Tomcat starting (allow 10-15 seconds for full startup)" "INFO"

# ============================================================================
# Verify Tomcat is Running
# ============================================================================
Write-Header "Verifying Deployment"

$maxAttempts = 15
$attempt = 0
$tomcatRunning = $false

while ($attempt -lt $maxAttempts) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            $tomcatRunning = $true
            break
        }
    } catch {
        # Still starting up, wait and retry
    }
    
    Write-Status "Waiting for Tomcat to start... ($($attempt + 1)/$maxAttempts)" "INFO"
    Start-Sleep -Seconds 1
    $attempt++
}

if ($tomcatRunning) {
    Write-Status "Tomcat is running and responding" "SUCCESS"
} else {
    Write-Status "Tomcat may still be starting up. Check logs at: $TOMCAT_HOME\logs\catalina.out" "WARNING"
}

# ============================================================================
# Test Backend API
# ============================================================================
Write-Header "Testing Backend API Endpoint"

Start-Sleep -Seconds 3

try {
    $apiUrl = "http://localhost:8080/api"
    Write-Status "Testing API endpoint: $apiUrl" "INFO"
    
    $response = Invoke-WebRequest -Uri "$apiUrl" -TimeoutSec 5 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 404) {
        Write-Status "API endpoint is accessible (404 for root path is expected)" "SUCCESS"
    } else {
        Write-Status "API endpoint responded with status: $($response.StatusCode)" "SUCCESS"
    }
} catch {
    Write-Status "Could not reach API yet. It may still be starting. Logs available at: $TOMCAT_HOME\logs" "WARNING"
}

# ============================================================================
# Summary
# ============================================================================
Write-Header "Build and Deployment Complete!"

Write-Host "
✓ Backend built and deployed
✓ Frontend built
✓ Tomcat started

╔════════════════════════════════════════════════════════════════╗
║                    LOCAL TEST INFORMATION                      ║
╠════════════════════════════════════════════════════════════════╣
║ Frontend URL:     http://localhost:3000                        ║
║ Backend API URL:  http://localhost:8080/api                    ║
║ Database:         localhost:1532 (MySQL - root/fire@1532)      ║
║ Environment:      local                                        ║
║                                                                ║
║ Tomcat Logs:      $TOMCAT_HOME\logs                          ║
║ Frontend Build:   $FRONTEND_DIR\build                         ║
║ Backend Target:   $BACKEND_DIR\target                         ║
╚════════════════════════════════════════════════════════════════╝

NEXT STEPS:
  1. Start the frontend development server (optional for local dev):
     cd '$FRONTEND_DIR'
     npm start

  2. Access the application:
     http://localhost:3000

  3. Check Tomcat logs if you encounter issues:
     type '$TOMCAT_HOME\logs\catalina.out'

  4. To stop Tomcat:
     $TOMCAT_HOME\bin\shutdown.bat

" -ForegroundColor Green

Write-Host "Build started at: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray
