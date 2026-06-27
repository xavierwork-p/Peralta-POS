@echo off
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":5174" ^| findstr "LISTENING"') do taskkill /PID %%p /F >nul 2>&1
taskkill /IM cloudflared.exe /F >nul 2>&1
echo Demostracion publica detenida.
