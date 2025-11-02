\
@ECHO OFF
SETLOCAL
set BASE_DIR=%~dp0
set WRAPPER_JAR=%BASE_DIR%\.mvn\wrapper\maven-wrapper.jar
IF NOT EXIST "%WRAPPER_JAR%" (
  mkdir "%BASE_DIR%\.mvn\wrapper" 2>NUL
  set WRAPPER_DL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
  where curl >NUL 2>&1
  IF %ERRORLEVEL% EQU 0 (
    curl -fsSL %WRAPPER_DL% -o "%WRAPPER_JAR%"
  ) ELSE (
    where wget >NUL 2>&1
    IF %ERRORLEVEL% EQU 0 (
      wget -q %WRAPPER_DL% -O "%WRAPPER_JAR%"
    ) ELSE (
      echo Please install curl or wget to download Maven Wrapper
      exit /B 1
    )
  )
)
set JAVA_CMD=java
"%JAVA_CMD%" -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
