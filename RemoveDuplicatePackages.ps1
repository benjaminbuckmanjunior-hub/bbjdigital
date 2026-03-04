# Remove duplicate package declarations from Java files

param(
    [string]$SourceDir = "C:\Users\Buckman\Desktop\BBJ digital\backend\src"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Removing Duplicate Package Declarations"
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$javaFiles = Get-ChildItem -Path $SourceDir -Recurse -Filter "*.java" | Where-Object { $_.FullName -like "*\com\example\*" }
Write-Host "Processing $($javaFiles.Count) Java files" -ForegroundColor Green
$fixedCount = 0

foreach ($file in $javaFiles) {
    try {
        $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
        $originalContent = $content
        
        # Remove leading blank lines before first package declaration
        $content = $content -replace "^(\r?\n)+", ""
        
        # Remove duplicate consecutive package declarations
        $content = $content -replace "(package\s+[^;]+;\s*\n)\1+", "$1"
        
        # Remove package declarations followed immediately by another package declaration
        $content = $content -replace "package\s+[^;]+;\s*\n\s*package\s+", "package "
        
        if ($content -ne $originalContent) {
            [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.UTF8Encoding]::new($false))
            Write-Host "  + Cleaned: $($file.Name)"
            $fixedCount++
        }
    }
    catch {
        Write-Host "  ! Error processing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Duplicate Package Removal Complete!" -ForegroundColor Green
Write-Host "Fixed: $fixedCount files" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
