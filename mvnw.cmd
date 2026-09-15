@REM ----------------------------------------------------------------------------
@REM Maven Wrapper script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "WRAPPER_DIR=%~dp0"
set "WRAPPER_PROPERTIES=%WRAPPER_DIR%\.mvn\wrapper\maven-wrapper.properties"

for /f "tokens=2 delims==" %%a in ('findstr "^distributionUrl=" "%WRAPPER_PROPERTIES%"') do set "DISTRIBUTION_URL=%%a"

if not defined MAVEN_USER_HOME set "MAVEN_USER_HOME=%USERPROFILE%\.m2"
set "WRAPPER_DIR_CACHE=%MAVEN_USER_HOME%\wrapper\dists"
set "TARGET_DIR=%WRAPPER_DIR_CACHE%\apache-maven-3.9.6"
set "MVN_EXEC=%TARGET_DIR%\bin\mvn.cmd"

if not exist "%MVN_EXEC%" (
    echo Downloading Maven from %DISTRIBUTION_URL% ...
    powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%TEMP%\maven.zip'; Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%WRAPPER_DIR_CACHE%'; Remove-Item '%TEMP%\maven.zip'"
)

call "%MVN_EXEC%" %*
