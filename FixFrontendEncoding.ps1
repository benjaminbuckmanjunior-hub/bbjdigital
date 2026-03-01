# Fix encoding for frontend JSON files

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Fixing Frontend File Encoding"
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$frontendDir = "C:\Users\Buckman\Desktop\BBJ digital\frontend"
$jsonFiles = Get-ChildItem -Path $frontendDir -Recurse -Filter "*.json" | Where-Object { $_.FullName -notlike "*node_modules*" -and $_.FullName -notlike "*.package*" }

Write-Host "Found $($jsonFiles.Count) JSON files to process" -ForegroundColor Green

foreach ($file in $jsonFiles) {
    try {
        $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
        
        if ([string]::IsNullOrEmpty($content)) {
            $content = Get-Content -Path $file.FullName -Raw -ErrorAction SilentlyContinue
        }
        
        # Remove BOM if present
        if ($content -and $content[0] -eq 0xFEFF) {
            $content = $content.Substring(1)
            Write-Host "  ! Removed BOM: $($file.Name)"
        }
        
        [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.UTF8Encoding]::new($false))
        Write-Host "  ✓ Fixed: $($file.Name)"
    }
    catch {
        Write-Host "  ✗ Error: $($file.Name) - $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Frontend Encoding Fix Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
