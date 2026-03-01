# Fix Java File Encoding Issues
# Converts files with UTF-16 BOM to UTF-8 without BOM

param(
    [string]$SourceDir = "C:\Users\Buckman\Desktop\BBJ digital\backend\src"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Fixing Java File Encoding"
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get all Java files
$javaFiles = Get-ChildItem -Path $SourceDir -Recurse -Filter "*.java" -ErrorAction SilentlyContinue

Write-Host "Found $($javaFiles.Count) Java files" -ForegroundColor Green
Write-Host ""

$fixedCount = 0

foreach ($file in $javaFiles) {
    try {
        # Read the file content
        $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8 -ErrorAction SilentlyContinue
        
        # If it failed or appears to have BOM issues, try reading as default (which handles BOM)
        if ([string]::IsNullOrEmpty($content)) {
            $content = Get-Content -Path $file.FullName -Raw -ErrorAction SilentlyContinue
        }
        
        # Remove BOM if present (0xFEFF)
        if ($content[0] -eq 0xFEFF) {
            $content = $content.Substring(1)
            Write-Host "  Removing BOM from: $($file.Name)" -ForegroundColor Yellow
        }
        
        # Write back as UTF-8 without BOM
        [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.UTF8Encoding]::new($false))
        $fixedCount++
        Write-Host "  ? Fixed: $($file.Name)" -ForegroundColor Green
    }
    catch {
        Write-Host "  ? Error fixing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Encoding Fix Complete!" -ForegroundColor Green
Write-Host "Fixed: $fixedCount files" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
