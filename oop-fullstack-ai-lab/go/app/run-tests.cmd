@echo off
set JAVA_HOME=C:\Program Files\AdoptOpenJDK\jdk-17.0.0.20-hotspot
cd /d C:\Users\User\Desktop\oop-aifullstack-lab\oop-aifullstack-lab\go\app
C:\Users\User\AppData\Local\Programs\Apache\Maven\apache-maven-3.9.11\bin\mvn.cmd -q test
if %ERRORLEVEL% equ 0 (
  echo SUCCESS
) else (
  echo FAILURE
  echo ERRORLEVEL=%ERRORLEVEL%
)
