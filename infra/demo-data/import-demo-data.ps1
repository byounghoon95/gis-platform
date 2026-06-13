param(
    [string] $ApiBaseUrl = "http://localhost:8080",
    [string] $AdminEmail = "admin@example.com",
    [string] $AdminPassword = "admin1234"
)

$ErrorActionPreference = "Stop"

$DemoDataDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$Headers = @{ "Content-Type" = "application/json" }
$LoginBody = @{
    email = $AdminEmail
    password = $AdminPassword
} | ConvertTo-Json

Write-Host "Logging in to $ApiBaseUrl as $AdminEmail"
$LoginResponse = Invoke-RestMethod -Method Post -Uri "$ApiBaseUrl/api/auth/login" -Headers $Headers -Body $LoginBody
$AuthHeader = "Bearer $($LoginResponse.accessToken)"

Write-Host "Creating sample candidate locations"
Import-Csv "$DemoDataDir/locations.csv" | ForEach-Object {
    $LocationBody = @{
        name = $_.name
        businessType = $_.business_type
        address = $_.address
        latitude = [decimal] $_.latitude
        longitude = [decimal] $_.longitude
        rentPrice = [int] $_.rent_price
        memo = $_.memo
    } | ConvertTo-Json

    Invoke-RestMethod `
        -Method Post `
        -Uri "$ApiBaseUrl/api/admin/locations" `
        -Headers @{ "Authorization" = $AuthHeader; "Content-Type" = "application/json" } `
        -Body $LocationBody | Out-Null
}

$Uploads = @(
    @{ Path = "$DemoDataDir/facilities.csv"; Endpoint = "facilities" },
    @{ Path = "$DemoDataDir/competitors.csv"; Endpoint = "competitors" },
    @{ Path = "$DemoDataDir/transit-stops.csv"; Endpoint = "transit-stops" },
    @{ Path = "$DemoDataDir/foot-traffic.csv"; Endpoint = "foot-traffic" }
)

foreach ($Upload in $Uploads) {
    Write-Host "Uploading $($Upload.Endpoint)"
    $Response = curl.exe -sS `
        -H "Authorization: $AuthHeader" `
        -F "file=@$($Upload.Path)" `
        "$ApiBaseUrl/api/admin/uploads/$($Upload.Endpoint)"

    $Parsed = $Response | ConvertFrom-Json
    if ($Parsed.errors.Count -gt 0) {
        throw "Upload failed for $($Upload.Endpoint): $Response"
    }

    Write-Host "Inserted $($Parsed.insertedRows) of $($Parsed.totalRows) rows into $($Upload.Endpoint)"
}

Write-Host "Demo data import complete."
