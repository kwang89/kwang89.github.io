## IntelliJ 가이드

### 프로젝트 생성
- New Project → Language: Java → JDK 11+ 선택 → Create

### 실행/디버깅
- Run ▶ 실행 → 콘솔에 문장을 입력하고 Enter
- 또는 Run Configuration의 "Modify options → Redirect input"을 켜고 테스트 문자열 파일을 연결

### 코드/스타일
- 인코딩: File Encodings → Global/Project 모두 UTF-8
- Line Separator: `\n` 유지(기본)

### 2·3번 대비(의존성)
- 이후 문제가 HTTP/JSON을 요구하면:
  - Gson 2.10.1 추가
  - Jetty 9(서버/클라이언트) 추가
- 방법: Gradle/Maven 사용 권장 또는 Project Structure → Libraries에 JAR 수동 추가

---

## JAR 라이브러리 수동 추가 방법

프로젝트에 빌드 도구가 없을 때(순수 Java 프로젝트) JAR를 직접 추가하는 절차입니다.

1) 메뉴 경로: `File > Project Structure... (⌘; / Ctrl+;)`
2) 좌측 `Project Settings > Libraries`
3) `+` 버튼 → `Java` → 추가할 JAR 파일 선택 → `Apply`
4) 모듈에 연결 확인: 좌측 `Project Settings > Modules` → 탭 `Dependencies`에 방금 추가한 라이브러리가 있어야 함

참고
- 여러 JAR을 한 번에 추가 가능
- 삭제/교체는 같은 화면에서 관리

---

## 실행/디버깅 방법(메뉴 경로 포함)

### 실행 구성 만들기
- 메뉴: `Run > Edit Configurations...`
- `+` → `Application`
  - `Name`: 자유롭게, 예) Problem1
  - `Main class`: `com.example.problem1.Problem1Main`
  - `Working directory`: 프로젝트 루트(상대경로 사용 시 중요)
  - `Environment`: 필요 시 JVM 옵션/환경변수 지정
  - `Program arguments`: 프로그램 인자 필요 시 입력
  - `Apply > OK`

### 실행
- 메뉴: `Run > Run 'Problem1'` (단축키: ⌃R / Shift+F10)
- 툴바의 ▶ 버튼 클릭으로도 실행 가능

### 디버깅
- 브레이크포인트: 코드 왼쪽 거터 클릭
- 메뉴: `Run > Debug 'Problem1'` (단축키: ⌃D / Shift+F9)
- 디버그 창에서 변수/스택/Watch/Evaluate 사용

### 입력 리다이렉션(콘솔 입력 자동화)
- 메뉴: `Run > Edit Configurations... > Problem1`
- `Modify options` 클릭 → `Redirect input from` 체크 → 테스트 파일 경로 지정

---

## 주요 단축키(기본 Keymap 기준)

| 작업 | macOS | Windows/Linux |
|---|---|---|
| 실행 | ⌃R | Shift+F10 |
| 디버그 시작 | ⌃D | Shift+F9 |
| 브레이크포인트 토글 | ⌘F8 | Ctrl+F8 |
| 현재 줄 실행(Step Over) | F8 | F8 |
| 함수 안으로(Step Into) | F7 | F7 |
| 다음 브레이크까지 계속(Resume) | ⌥⌘R | F9 |
| 검색(파일 내) | ⌘F | Ctrl+F |
| 전체 검색(프로젝트) | ⌘⇧F | Ctrl+Shift+F |
| 파일/클래스/기호 탐색 | ⌘O / ⌘⇧O / ⌘⌥O | Ctrl+N / Ctrl+Shift+N / Ctrl+Alt+Shift+N |
| 리팩터링(이름 변경) | ⇧F6 | Shift+F6 |
| 코드 포맷 | ⌥⌘L | Ctrl+Alt+L |
| 빠른 수정(인텐션) | ⌥⏎ | Alt+Enter |
| 최근 파일 | ⌘E | Ctrl+E |
| 터미널 열기 | ⌥F12 | Alt+F12 |

팁
- Keymap은 `Preferences > Keymap`(macOS) / `Settings > Keymap`(Windows/Linux)에서 개인화 가능합니다.


---

## JAR 빌드 방법(Artifacts)

빌드 도구 없이 IntelliJ만으로 실행 JAR를 만드는 절차입니다.

### 1) 일반 JAR(클래스만 포함)
1. 메뉴: `File > Project Structure...`
2. 좌측: `Project Settings > Artifacts`
3. `+` 버튼 → `JAR > From modules with dependencies...`
4. `Main Class`: `com.example.problem1.Problem1Main` 지정 → `OK`
5. `Output directory` 확인/수정 → `Apply > OK`
6. 빌드: `Build > Build Artifacts... > Problem1:jar > Build`

실행 예:
```bash
java -jar out/artifacts/Problem1/Problem1.jar
```

주의: 외부 라이브러리 JAR은 포함되지 않을 수 있습니다(환경에 따라). 이 경우 클래스패스에 별도로 추가하거나 Fat JAR을 사용하세요.

### 2) Fat JAR(의존성 포함)
1. 위 Artifacts 화면에서 `Output Layout` 탭으로 이동
2. 오른쪽 트리에서 의존 JAR을 선택 후 `Extract...` 또는 `Put into the output root`(버전별 명칭 상이)로 실행 JAR에 통합
3. `Apply > OK` → `Build > Build Artifacts... > Build`

복잡할 경우 Gradle의 `shadowJar` 또는 Maven의 `maven-shade-plugin` 사용을 권장합니다.


