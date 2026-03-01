# Add package declarations to Java files that are missing them

param(
    [string]$SourceDir = "C:\Users\Buckman\Desktop\BBJ digital\backend\src"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Adding Package Declarations"
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$javaFiles = Get-ChildItem -Path $SourceDir -Recurse -Filter "*.java" | Where-Object { $_.FullName -like "*\com\example\*" }
Write-Host "Found $($javaFiles.Count) Java files to process" -ForegroundColor Green
$fixedCount = 0

foreach ($file in $javaFiles) {
    try {
        $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
        
        # Check if file already has package declaration
        if ($content -match "^\\s*package\\s+") {
            Write-Host "  - $($file.Name) already has package declaration"
            continue
        }
        
        # Determine package based on directory structure
        $relativePath = $file.FullName.Substring($SourceDir.Length).Replace("\", "/")
        $dirParts = $relativePath.Split("/") | Where-Object { $_ -and $_ -ne "$($file.BaseName).java" }
        $package = $dirParts -join "."
        $package = $package.TrimEnd("/")
        
        if ($package -and $package -ne "") {
            # Add package declaration at the top
            $newContent = "package $package;" + [Environment]::NewLine + [Environment]::NewLine + $content
            [System.IO.File]::WriteAllText($file.FullName, $newContent, [System.Text.UTF8Encoding]::new($false))
            Write-Host "  + $($file.Name) -> package $package"
            $fixedCount++
        }
    }
    catch {
        Write-Host "  ! Error processing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Package Declaration Fix Complete!" -ForegroundColor Green
Write-Host "Fixed: $fixedCount files" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
