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


