## Windows 배치 파일 (.bat)

기본 구조:

```bat
@echo off
REM 인사 출력
echo Hello from batch!
REM 일시 정지
pause
```

환경변수와 인자:

```bat
@echo off
set NAME=%1
echo Hi %NAME%
```

관리자 권한 확인:

```bat
@echo off
net session >nul 2>&1
if %errorlevel% neq 0 (
  echo 관리자 권한이 필요합니다.
  exit /b 1
)
echo 관리자 권한으로 실행 중
```

### %~dp0 설명
`%~dp0`는 현재 배치 스크립트 파일의 디렉토리 경로(뒤에 `\` 포함)를 의미합니다. 현재 작업 디렉토리(`%cd%`)와 무관하게, 스크립트가 위치한 폴더를 기준으로 경로를 안전하게 참조할 수 있습니다.


### JAR 실행 (고정 파일명)

```bat
@echo off
setlocal

REM 동일 폴더의 app.jar 실행
set JAR_PATH=%~dp0app.jar
if not exist "%JAR_PATH%" (
  echo JAR가 존재하지 않습니다: %JAR_PATH%
  exit /b 1
)

java -jar "%JAR_PATH%" %*
endlocal
```

### 최신 JAR 자동 실행 (build\*.jar 중 가장 최근 파일)

```bat
@echo off
setlocal ENABLEDELAYEDEXPANSION

REM 스크립트 기준 build 폴더에서 최신 JAR 찾기
set BUILD_DIR=%~dp0build
if not exist "%BUILD_DIR%" (
  echo build 폴더가 없습니다: %BUILD_DIR%
  exit /b 1
)

set LATEST_JAR=
for /f "delims=" %%f in ('dir /b /a:-d /o:-d "%BUILD_DIR%\*.jar"') do (
  set LATEST_JAR=%%f
  goto :found
)

:found
if not defined LATEST_JAR (
  echo JAR 파일을 찾지 못했습니다: %BUILD_DIR%
  exit /b 1
)

set JAR_PATH=%BUILD_DIR%\%LATEST_JAR%
echo 실행: %JAR_PATH%
java -jar "%JAR_PATH%" %*
endlocal
```

### 클래스패스 실행 (라이브러리 포함)

```bat
@echo off
setlocal

REM libs 폴더의 모든 JAR과 현재 디렉토리를 클래스패스에 포함
set CP=.;%~dp0libs\*

REM Main 클래스(예: com.example.Main) 실행
set MAIN_CLASS=com.example.Main

java -cp "%CP%" %MAIN_CLASS% %*
endlocal
```

### JAVA_HOME 사용 및 버전 체크 예시

```bat
@echo off
setlocal

REM JAVA_HOME이 설정되어 있으면 해당 java 사용
if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXE=java
)

"%JAVA_EXE%" -version
if %errorlevel% neq 0 (
  echo Java 실행 파일을 찾지 못했습니다. JAVA_HOME을 설정하거나 PATH를 확인하세요.
  exit /b 1
)

REM 실제 실행 (예: app.jar)
"%JAVA_EXE%" -jar "%~dp0app.jar" %*
endlocal
```

### 상대경로 예제

```bat
@echo off
REM 스크립트 폴더 기준 상대경로 JAR 실행 (예: scripts\run.bat, jar는 scripts\..\dist\app.jar)
set JAR_PATH=%~dp0..\dist\app.jar
if not exist "%JAR_PATH%" (
  echo JAR가 없습니다: %JAR_PATH%
  exit /b 1
)
java -jar "%JAR_PATH%" %*
```

현재 작업 디렉토리 기준(사용자가 `cd`로 이동한 위치 기준)으로 상대경로 JAR 실행 예:

```bat
@echo off
REM 현재 작업 디렉토리 기준 상대경로 (예: .\dist\app.jar)
set JAR_PATH=.\dist\app.jar
if not exist "%JAR_PATH%" (
  echo JAR가 없습니다: %JAR_PATH%
  exit /b 1
)
java -jar "%JAR_PATH%" %*
```

권장: 실행 파일과 리소스가 함께 배포된다면 `%~dp0` 기반 경로를 사용하면 사용자의 현재 폴더에 영향받지 않아 안전합니다.


