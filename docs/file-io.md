## 문제 1 — 파일 입출력 정리(DICTIONARY.TXT)

### 파일 위치와 인코딩
- 위치: 실행 디렉터리 기준 상대경로 `./DICTIONARY.TXT`
- 인코딩: UTF-8(윈도우에서도 UTF-8 권장)

### 행 형식
- `<단어>#<정수,정수,정수>`
- 단어는 소문자만 사용(사전 규칙)
- 오른쪽 값은 "정수,정수,정수" 형태의 **문자열**이며, 숫자 파싱하지 않고 그대로 사용
- 빈 줄, `#`로 시작하는 주석 줄 허용(무시됨)

### 예시 파일
```text
# DICTIONARY.TXT (UTF-8)
a#0,0,0
an#0,0,0
the#0,0,0
good#7,112,9816
mary#0,0,1
had#1,0,0
little#0,23,0
lamb#11,223,17
```

### 읽기 전략 요약
- `Files.newBufferedReader(path, UTF_8)` 사용
- 행 단위 반복 → `trim()` → 빈 줄/주석 줄 건너뜀
- `split("#", 2)` 후 오른쪽을 **그대로** 보관(`EmbeddingVector(csv)`)
- 최소 형식만 점검(콤마 포함 여부). 잘못된 행은 조용히 무시

### 리소스 정리/예외
- try-with-resources로 파일 핸들 자동 해제
- 파일 부재 시 즉시 예외 처리로 실패를 알림(대기 없이 종료)

### 전처리 시 조회 규칙
- 입력 토큰을 소문자 변환하여 사전을 조회
- 사전에 없으면 입력한 단어 원형 그대로 출력(정책)

### 테스트 팁
- BOM 없는 UTF-8로 저장
- 파일 끝 줄바꿈과 공백 문자 차이(스페이스/탭) 주의
- 대용량 사전에서도 `HashMap` 조회는 평균 O(1)

---

## 파일 입출력 패턴 모음(실전 템플릿)

다음 예제들은 문제 범위를 넘어, 일반적으로 많이 쓰는 안전한 파일 I/O 패턴입니다. 모두 UTF-8 기준이며, 리소스는 try-with-resources로 관리합니다.

태그 안내:
- [추천]: 실무에서 자주 쓰이고 안전성이 높은 기본 패턴
- [범용]: 다양한 상황에 널리 적용 가능
- [대용량]: 큰 파일 처리에 적합
- [소형]: 작은 파일에 적합(간단/빠름)
- [고급]: 특수 목적/세밀 제어가 필요한 경우

추천 요약:
- 텍스트 사전/설정 파일 읽기: 0) 라인 기반 파싱 [추천][범용]
- 일반 텍스트 쓰기: 3) 기본 쓰기(덮어쓰기) [추천][범용]
- 설정/결과 파일의 안전 저장: 6) 안전한 쓰기(임시→원자적 교체) [가장 안전]

### 0) 라인 기반 텍스트 파일 파싱(사전/키-값 파일) [추천][범용]
```java
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

Path path = Paths.get("DICTIONARY.TXT");
Map<String, String> map = new HashMap<>();
try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    String line;
    while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) continue; // 빈 줄/주석
        String[] kv = line.split("#", 2);
        if (kv.length != 2) continue;                          // 형식 오류 무시
        map.put(kv[0], kv[1]);                                 // 오른쪽 CSV 문자열 그대로 저장
    }
}
```

### 1) 작은 파일 전체 읽기(String) [소형]
```java
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

Path p = Paths.get("input.txt");
String content = Files.readString(p, StandardCharsets.UTF_8); // 파일 전체를 문자열로
```

### 2) 큰 파일 라인 스트리밍 처리 [대용량][범용]
```java
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

Path p = Paths.get("large.txt");
try (java.util.stream.Stream<String> lines = Files.lines(p, StandardCharsets.UTF_8)) {
    lines.filter(s -> !s.isBlank())
         .forEach(System.out::println);
}
```

### 3) 기본 쓰기(덮어쓰기) [추천][범용]
```java
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

Path p = Paths.get("output.txt");
Files.writeString(p, "hello\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
// CREATE: 없으면 생성, TRUNCATE_EXISTING: 있으면 길이 0으로 만들고 다시 씀
```

### 4) 이어쓰기(Append) [로그/추가]
```java
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

Path p = Paths.get("output.txt");
Files.writeString(p, "append line\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
```

### 5) 존재하지 않는 디렉터리까지 생성 후 쓰기 [추천]
```java
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

Path p = Paths.get("logs/app/2025-01-01.log");
Files.createDirectories(p.getParent()); // 상위 디렉터리들까지 생성
Files.writeString(p, "log start\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
```

### 6) 안전한 쓰기(임시 파일 → 원자적 교체) [가장 안전][운영추천]
```java
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

Path target = Paths.get("config.json");
Path tmp = Paths.get("config.json.tmp");
Files.writeString(tmp, "{}\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
// ATOMIC_MOVE: 가능한 경우 원자적 교체로 중간 손상 방지
```

### 7) 파일이 없을 때의 예외 처리 [필수]
```java
import java.nio.file.*;

Path p = Paths.get("missing.txt");
try {
    byte[] bytes = Files.readAllBytes(p);
} catch (NoSuchFileException e) {              // 경로가 존재하지 않을 때
    System.err.println("파일 없음: " + e.getFile());
} catch (java.io.IOException e) {               // 기타 I/O 문제(권한 등)
    e.printStackTrace();
}
```

### 8) 파일 존재 여부/삭제/복사 [유틸]
```java
import java.nio.file.*;

Path a = Paths.get("a.txt");
Path b = Paths.get("b.txt");
boolean exists = Files.exists(a);
if (exists) {
    Files.copy(a, b, StandardCopyOption.REPLACE_EXISTING); // 복사
    Files.delete(a);                                        // 삭제
}
```

### 9) 바이너리 읽기/쓰기 [바이너리]
```java
import java.nio.file.*;

byte[] data = Files.readAllBytes(Paths.get("image.png"));
Files.write(Paths.get("copy.png"), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
```

### 10) 전통적 버퍼 스트림(세밀 제어가 필요할 때) [고급]
```java
import java.io.*;
import java.nio.file.*;

try (InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get("in.bin")));   // 자동 close
     OutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get("out.bin"),
         StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
    byte[] buf = new byte[8192];
    int n;
    while ((n = in.read(buf)) > 0) {
        out.write(buf, 0, n);
    }
}
```

### 11) 문자 인코딩 주의사항
- `StandardCharsets.UTF_8`를 명시적으로 사용하여 OS 기본 인코딩 차이를 제거
- Windows에서 편집 시에도 파일을 UTF-8(BOM 없이)로 저장

---

## 문제와의 연결 고리(요약)
- 사전 파일은 `Files.newBufferedReader(..., UTF_8)`로 열고, 줄 단위로 파싱
- 오른쪽 값은 문자열 CSV 그대로 보관하여 변환 손실 없음
- 파일 부재/형식 오류는 프로그램의 실행 완결성을 해치지 않도록 조용히 건너뛰거나 명확히 실패 처리

---

## OS별 차이와 실무 팁

### 경로 구분자
- Windows: `\\`(백슬래시), Unix/macOS: `/`(슬래시)
- Java의 `Paths.get(...)`/`Path.resolve(...)` 사용 시 OS별 구분자를 자동 처리하므로 문자열 상수로 경로를 붙이지 말고 항상 `Path` API를 사용하세요.

### 줄바꿈/인코딩
- 줄바꿈: Windows `\r\n`, Unix/macOS `\n`. `Files.readString/lines`는 자동 처리하지만, 문자열 리터럴 합성 시 주의.
- 인코딩: OS 기본값이 다를 수 있으므로 항상 `StandardCharsets.UTF_8`을 명시.

### 권한/락킹
- Windows는 파일이 열려 있는 동안 이동/삭제가 제한될 수 있음. 쓰기 전 `try-with-resources`로 스트림을 명확히 닫은 뒤 이동/삭제를 수행.
- Unix권한(모드) 제어가 필요하면 `Files.setPosixFilePermissions`(POSIX 지원 FS 한정)을 사용.

### 대소문자
- Windows 기본 파일시스템은 보통 대소문자 구분 약함(case-insensitive), Unix/macOS는 구분하는 경우가 많음. 파일명 비교는 OS에 의존하지 않도록 엄격히 동일 문자열만 사용.

### 임시 디렉터리/사용자 홈
```java
Path tmp = Paths.get(System.getProperty("java.io.tmpdir"));
Path home = Paths.get(System.getProperty("user.home"));
```

### 안전 로그/경로 출력
- 경로는 `path.toAbsolutePath()`로 풀어 출력하면 디버깅에 유리.

### 프로젝트 루트 표기(시작점) 표

| 구분 | 루트(시작) 의미 | 콘솔/문서에서의 표기 예 | 비고 |
|---|---|---|---|
| 네이티브 Java | 명령을 실행한 현재 작업 디렉터리(Current Working Directory) | `./DICTIONARY.TXT`, `./out`, `java -cp out ...` | `java`/`jar` 실행 위치가 기준 |
| Spring Boot (Gradle/Maven) | 애플리케이션 실행 시의 작업 디렉터리(일반적으로 프로젝트 루트) | `./config/app.yml`, `--spring.config.additional-location=./config/` | Jar로 실행 시에도 기본은 실행한 위치 |
| Spring Boot (리소스 내부) | 클래스패스(classpath) 루트 | `classpath:application.yml`, `classpath:dictionary.txt` | `src/main/resources` 아래가 포함됨 |
| OS 절대 경로 | 파일시스템 절대 경로 | `C:\\data\\file.txt`, `/var/app/file.txt` | OS별 경로 구분자 주의 |

참고
- “상대경로”는 항상 “현재 작업 디렉터리” 기준입니다. IDE/배포 환경별로 작업 디렉터리가 달라질 수 있으므로, 필요하면 작업 디렉터리를 명시적으로 설정하거나(예: IntelliJ Run Configuration의 Working directory), Spring Boot에선 `classpath:` 리소스를 활용하세요.

---

## 추가 I/O 패턴(고급)

### 메모리 맵 파일(대용량/랜덤액세스)
```java
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

try (FileChannel ch = FileChannel.open(Paths.get("big.bin"), StandardOpenOption.READ)) {
    MappedByteBuffer mb = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size());
    byte b = mb.get(0); // 랜덤 액세스
}
```

### 임시 파일 생성/자동 삭제 훅
```java
Path tmp = Files.createTempFile("app-", ".tmp");
tmp.toFile().deleteOnExit();
```

### 파일 변경 감시(WatchService)
```java
import java.nio.file.*;

WatchService ws = FileSystems.getDefault().newWatchService();
Path dir = Paths.get("logs");
dir.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
for (;;) {
    WatchKey key = ws.take();
    for (WatchEvent<?> e : key.pollEvents()) {
        System.out.println("changed: " + e.context());
    }
    key.reset();
}
```


