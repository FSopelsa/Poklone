@echo off
setlocal

if not defined POKLONE_JAVA_HOME (
    set "POKLONE_JAVA_HOME=%USERPROFILE%\.jdks\jdk-21.0.12+8"
)

call "%~dp0mvn.cmd" -q test
if errorlevel 1 exit /b %ERRORLEVEL%

"%POKLONE_JAVA_HOME%\bin\java.exe" -cp "%~dp0target\classes" se.poklone.Main %*
