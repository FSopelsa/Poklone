@echo off
setlocal

if not defined POKLONE_JAVA_HOME (
    set "POKLONE_JAVA_HOME=%USERPROFILE%\.jdks\jdk-21.0.12+8"
)

if not exist "%POKLONE_JAVA_HOME%\bin\java.exe" (
    echo Poklone requires Temurin JDK 21.0.12+8. 1>&2
    echo Expected it at "%POKLONE_JAVA_HOME%". 1>&2
    echo Set POKLONE_JAVA_HOME to another JDK 21 installation if needed. 1>&2
    exit /b 1
)

set "JAVA_HOME=%POKLONE_JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
call "%~dp0mvnw.cmd" %*
set "MVN_EXIT_CODE=%ERRORLEVEL%"

endlocal & exit /b %MVN_EXIT_CODE%
