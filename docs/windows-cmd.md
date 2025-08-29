## Windows 명령프롬프트 빌드/실행 가이드

### 전제
- Windows 10/11, JDK 11 이상 설치, `java`/`javac`가 PATH에 등록
- 작업 폴더 구조(예):
```
project\
  src\main\java\com\example\problem1\...
  DICTIONARY.TXT
```

### 1) 컴파일(UTF-8, 출력 디렉터리 분리)
```bat
cd project
javac -encoding UTF-8 -d out ^
  src\main\java\com\example\problem1\model\EmbeddingVector.java ^
  src\main\java\com\example\problem1\io\Dictionary.java ^
  src\main\java\com\example\problem1\io\DictionaryLoader.java ^
  src\main\java\com\example\problem1\nlp\Tokenizer.java ^
  src\main\java\com\example\problem1\Problem1Main.java
```

### 2) 실행(클래스 경로 지정)
```bat
java -cp out com.example.problem1.Problem1Main
```

입력 예시:
```bat
echo Mary had a little lamb | java -cp out com.example.problem1.Problem1Main
```

예상 출력(사전이 위 예시와 동일할 때):
```text
0,0,1 1,0,0 0,0,0 0,23,0 11,223,17
```

### 3) 실행 JAR 만들기
```bat
jar cfe app.jar com.example.problem1.Problem1Main -C out .
```

실행:
```bat
java -jar app.jar
```

주의: `DICTIONARY.TXT`는 **실행 디렉터리**에 존재해야 함. JAR 옆에 두고 `java -jar app.jar`을 실행하면 같은 디렉터리의 `DICTIONARY.TXT`가 사용됨.

### 문제 해결 체크리스트
- **파일 인코딩**: 소스/사전 모두 UTF-8
- **작업 디렉터리**: `DICTIONARY.TXT`의 상대경로 기준 확인
- **공백 규칙**: 입력은 단어 사이 공백 1칸(문제 명세)


---

## 배치 스크립트로 SP_TEST 명령 만들기

목표: 명령창에 `SP_TEST`만 입력하면 프로그램이 실행되도록 배치 파일을 구성합니다.

### 1) 클래스 실행형 배치(개발 중 out 디렉터리 사용)
파일명: `SP_TEST.bat`
```bat
@echo off
setlocal enabledelayedexpansion
REM 콘솔 UTF-8 (필요 시)
chcp 65001 >nul
REM 현재 스크립트 기준 프로젝트 루트 계산(스크립트를 프로젝트 루트에 두는 경우는 . 로 충분)
set BASE=%~dp0
REM 실행 디렉터리를 프로젝트 루트로 설정
pushd "%BASE%"
REM DICTIONARY.TXT는 실행 디렉터리 기준 상대경로로 읽힘
java -cp out com.example.problem1.Problem1Main %*
set EXITCODE=%ERRORLEVEL%
popd
exit /b %EXITCODE%
```

사용법:
```bat
SP_TEST
```
파이프 입력 테스트:
```bat
echo Mary had a little lamb | SP_TEST
```

### 2) JAR 실행형 배치(배포용)
실행 JAR를 `app.jar`로 만들었다고 가정합니다. `SP_TEST.bat` 내용:
```bat
@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
set BASE=%~dp0
pushd "%BASE%"
java -jar app.jar %*
set EXITCODE=%ERRORLEVEL%
popd
exit /b %EXITCODE%
```

### 3) 어디서나 실행되도록 PATH 등록
1. `C:\tools\sp_test\` 같은 폴더를 만들고 `SP_TEST.bat`(그리고 필요 시 `app.jar`)를 그 안에 둡니다.
2. 환경 변수 편집: `시스템 속성 > 고급 > 환경 변수 > Path > 편집 > 새로 만들기` → `C:\tools\sp_test\` 추가 → 확인
3. 새로 연 터미널에서 `where SP_TEST`로 경로 확인 후 실행합니다.

참고/주의
- `DICTIONARY.TXT`는 배치가 `pushd`로 이동한 실행 디렉터리(기본: 배치가 위치한 경로) 기준 상대경로로 읽힙니다. 배치 파일과 같은 폴더에 두는 것을 권장합니다.
- 콘솔이 한글/UTF-8을 제대로 표시하지 못할 때는 `chcp 65001`을 유지하세요.

### 명령 설명과 필수 여부
- setlocal enabledelayedexpansion: 현재 배치에서만 환경변경을 한정하고 지연 확장을 켭니다. 이 스크립트는 지연 확장을 사용하지 않으므로 필수는 아님. setlocal 자체도 선택.
- chcp 65001 >nul: 콘솔 코드페이지를 UTF-8로 전환. 환경에 따라 선택(인코딩 깨짐이 없으면 생략 가능). 권장.
- set BASE=%~dp0: 배치 파일이 위치한 절대 경로를 얻어 BASE에 저장. 배치 기준 상대경로를 안정적으로 쓰기 위해 권장.
- pushd "%BASE%": 작업 디렉터리를 BASE로 변경(나중에 popd로 복귀). `DICTIONARY.TXT`를 항상 찾도록 권장. 대신 아래 최소 스크립트처럼 `%~dp0`를 직접 경로에 넣으면 생략 가능.
- %*: 배치에 전달된 모든 인자를 Java 프로그램에도 그대로 전달. 선택.
- set EXITCODE / exit /b: 자바 종료 코드를 배치 종료 코드로 전달(자동화/파이프라인에 유용). 선택.

### 최소 스크립트(간단 버전)
폴더 이동 없이 배치 위치를 직접 경로에 사용합니다. 실행 위치에 따라 `DICTIONARY.TXT`를 못 찾을 수 있다는 점만 유의하세요.
```bat
@echo off
java -cp "%~dp0out" com.example.problem1.Problem1Main %*
```

### 안정 스크립트(추천)
위에서 제시한 `pushd` 기반 스크립트. 작업 디렉터리를 배치 폴더로 고정하여 `DICTIONARY.TXT`를 확실히 찾습니다.


