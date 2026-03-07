$body = @{
    client = "Test"
    tester = "Test"
    testerSignature = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
}

Invoke-WebRequest -Uri "http://localhost:8080/api/pdf/preview" -Method POST -ContentType "application/x-www-form-urlencoded" -Body $body -OutFile "test.pdf"

Write-Host "PDF preview test completed. Check test.pdf file."