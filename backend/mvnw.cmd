@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Batch Script for Arun's Developer Portfolio Backend
@REM ----------------------------------------------------------------------------
@IF "%DEBUG%"=="" @ECHO OFF
@SETLOCAL

SET "DIRNAME=%~dp0"
IF "%DIRNAME%"=="" SET "DIRNAME=."
SET "MAVEN_HOME=%DIRNAME%tools\apache-maven-3.9.6"

IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
) ELSE (
    mvn %*
)

IF ERRORLEVEL 1 EXIT /B 1
EXIT /B 0
