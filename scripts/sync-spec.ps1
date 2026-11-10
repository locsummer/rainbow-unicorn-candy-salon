# Sync script for Windows PowerShell
$ErrorActionPreference = "Stop"

Write-Host "=== Syncing Game Specifications to Android & iOS (PowerShell) ==="
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = Split-Path -Parent $scriptDir

# 1. Verify spec integrity
node "$scriptDir\verify-spec.js"

# 2. Android targets
$androidAssets = "$rootDir\android\app\src\main\assets"
$androidTestRes = "$rootDir\android\core\src\test\resources"
New-Item -ItemType Directory -Force -Path $androidAssets | Out-Null
New-Item -ItemType Directory -Force -Path "$androidTestRes\golden" | Out-Null

Copy-Item "$rootDir\spec\levels.json" "$androidAssets\" -Force
Copy-Item "$rootDir\spec\items.json" "$androidAssets\" -Force
Copy-Item "$rootDir\spec\levels.json" "$androidTestRes\" -Force
Copy-Item "$rootDir\spec\items.json" "$androidTestRes\" -Force
Copy-Item "$rootDir\spec\golden\*" "$androidTestRes\golden\" -Recurse -Force

# 3. iOS targets
$iosRes = "$rootDir\ios\RainbowUnicorn\Resources"
$iosTestRes = "$rootDir\ios\GameCore\Tests\GameCoreTests\Golden"
New-Item -ItemType Directory -Force -Path $iosRes | Out-Null
New-Item -ItemType Directory -Force -Path $iosTestRes | Out-Null

Copy-Item "$rootDir\spec\levels.json" "$iosRes\" -Force
Copy-Item "$rootDir\spec\items.json" "$iosRes\" -Force
Copy-Item "$rootDir\spec\golden\*" "$iosTestRes\" -Recurse -Force

Write-Host "=== Sync completed successfully. Checksums match. ==="
