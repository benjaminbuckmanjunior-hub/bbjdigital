#!/usr/bin/env pwsh
cd "c:\Users\Buckman\Desktop\BBJ digital"
Write-Host "Current directory: $(Get-Location)"
Write-Host "Staging files..."
git add backend/src/com/example/controller/RegisterController.java
git add backend/src/com/example/dao/MemberDAO.java
Write-Host "Checking git status..."
git status
Write-Host "Committing..."
git commit -m "Enhance registration error diagnostics - added SQL logging and error details to response"
Write-Host "Pushing to origin..."
git push origin main
Write-Host "Done!"
