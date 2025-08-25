## 파일 입출력 (Java)

try-with-resources를 사용해 파일을 열고 자동으로 닫히도록 구성합니다.

```java
// 공통 패턴: try-with-resources
// try(...) 블록이 끝나면 자원이 자동으로 close() 됩니다.
try (var br = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get("example.txt"))) {
  // 사용
}
```

### NIO 자동 닫힘 정리

- 편의 메서드는 내부에서 파일을 열고 즉시 닫습니다 → 별도 close 불필요
  - 예: `Files.readString`, `Files.readAllBytes`, `Files.readAllLines`, `Files.write`, `Files.copy(Path, Path)` 등
- 직접 리소스를 여는 경우는 명시적으로 닫아야 합니다 → try-with-resources 권장
  - 예: `Files.newBufferedReader/newBufferedWriter/newInputStream/newOutputStream`, `Files.lines`, `Files.list/walk`(반환 `Stream`은 close 필요), `DirectoryStream`, `FileChannel`, `AsynchronousFileChannel` 등

```java
// 자동으로 닫힘: 편의 메서드로 전체 내용을 읽는 경우
var text = java.nio.file.Files.readString(java.nio.file.Path.of("a.txt"));

// 직접 연 리소스는 닫아야 함: try-with-resources 사용
try (var in = java.nio.file.Files.newInputStream(java.nio.file.Path.of("a.txt"))) {
  // 바이트 단위로 읽기
}
```

### 1) 파일 읽기

#### 1-1. BufferedReader (기본, 문자 단위)

```java
// 문자 단위로 안전하게 읽는 기본 예제
// BufferedReader를 직접 열었으므로 try-with-resources로 자동 닫기
// UTF-8 인코딩 지정, 한 줄씩 읽어 처리
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadBuffered {
  public static void main(String[] args) throws java.io.IOException {
    Path path = Paths.get("example.txt");
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    }
  }
}
```

#### 1-2. Stream API로 줄 단위 처리

```java
// Stream<String>을 사용한 줄 단위 처리
// Files.lines()가 반환하는 Stream은 자원을 잡고 있으므로 close 필요
// try-with-resources로 Stream을 자동 닫기
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadStream {
  public static void main(String[] args) throws java.io.IOException {
    Path path = Paths.get("example.txt");
    try (var lines = Files.lines(path, StandardCharsets.UTF_8)) {
      lines.filter(l -> !l.isBlank()).forEach(System.out::println);
    }
  }
}
```

#### 1-3. InputStream (바이트 단위)

```java
// 바이트 단위 읽기: 텍스트가 아닌 바이너리 파일에도 사용 가능
// 8KB 버퍼로 반복 읽기, read()가 -1이면 EOF
// InputStream은 직접 열었으므로 try-with-resources로 닫기
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadBytes {
  public static void main(String[] args) throws java.io.IOException {
    Path path = Paths.get("example.bin");
    byte[] buffer = new byte[8192];
    int read;
    try (InputStream in = Files.newInputStream(path)) {
      while ((read = in.read(buffer)) != -1) {
        // 유효 데이터는 buffer[0..read) 범위
        // 여기에서 처리(예: 해시 계산, 문자열 변환, 네트워크 전송 등)
      }
    }
  }
}
```

#### 1-4. 옵션별 읽기 예제 (DELETE_ON_CLOSE 등)

```java
// 옵션 사용 예: DELETE_ON_CLOSE
// 스트림/채널이 닫힐 때 파일 삭제 시도
// 반드시 close(=try-with-resources 종료)가 되어야 삭제가 트리거됨
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ReadWithOptions {
  public static void main(String[] args) throws java.io.IOException {
    Path temp = Paths.get("temp-read.txt");
    // 읽기 후 자동 삭제 (DELETE_ON_CLOSE)
    OpenOption[] options = new OpenOption[] { StandardOpenOption.DELETE_ON_CLOSE };
    try (InputStream in = Files.newInputStream(temp, options)) {
      // 파일의 모든 바이트를 표준 출력으로 복사
      in.transferTo(System.out);
    }
    // 파일 삭제는 파일시스템/플랫폼에 따라 지연될 수 있음
  }
}
```

주의: 읽기 시에는 기본적으로 READ 모드이며, 대다수의 옵션은 쓰기에서 더 많이 사용됩니다.

### 2) 파일 쓰기

#### 2-1. 덮어쓰기 (기본: CREATE + TRUNCATE_EXISTING + WRITE)

```java
// 덮어쓰기(write) 기본 패턴
// CREATE: 없으면 생성, TRUNCATE_EXISTING: 기존 내용 비우고 새로 쓰기, WRITE: 쓰기 모드
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WriteOverwrite {
  public static void main(String[] args) throws java.io.IOException {
    Path path = Paths.get("example.txt");
    try (BufferedWriter writer = Files.newBufferedWriter(
        path,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE)) {
      writer.write("첫 줄\n");
      writer.write("둘째 줄\n");
    }
  }
}
```

#### 2-2. 이어쓰기 (APPEND)

```java
// 이어쓰기(append) 패턴
// 파일이 없으면 CREATE로 생성, 있으면 끝에 APPEND로 붙여 작성
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WriteAppend {
  public static void main(String[] args) throws java.io.IOException {
    Path path = Paths.get("example.txt");
    try (var writer = Files.newBufferedWriter(
        path,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND)) {
      writer.write("추가된 줄\n");
    }
  }
}
```

#### 2-3. 없으면 생성, 있으면 실패 (CREATE_NEW)

```java
// CREATE_NEW: 파일이 이미 존재하면 예외 발생(덮어쓰지 않도록 보장)
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WriteCreateNew {
  public static void main(String[] args) throws java.io.IOException {
    Path path = Paths.get("new-file.txt");
    try (var writer = Files.newBufferedWriter(
        path,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE_NEW)) {
      writer.write("처음 생성될 때만 씁니다\n");
    }
  }
}
```

#### 2-4. 디스크 동기화 (SYNC/DSYNC)

```java
// DSYNC/SYNC: 쓰기 내구성 강화(성능 비용 증가)
// DSYNC: 데이터 동기화, SYNC: 메타데이터까지 동기화(파일시스템에 따라 동작/성능 차이)
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.OutputStream;

public class WriteSync {
  public static void main(String[] args) throws java.io.IOException {
    Path path = Paths.get("critical.log");
    // SYNC/DSYNC: 메타데이터/데이터를 디스크에 동기화하여 내구성 강화 (성능 저하 가능)
    try (OutputStream out = Files.newOutputStream(
        path,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.DSYNC)) {
      out.write("중요 로그".getBytes(java.nio.charset.StandardCharsets.UTF_8));
      out.flush();
    }
  }
}
```

옵션 요약
- CREATE: 파일이 없으면 생성
- CREATE_NEW: 파일이 있으면 예외 발생
- WRITE: 쓰기 모드
- TRUNCATE_EXISTING: 기존 내용을 비움
- APPEND: 파일 끝에 이어쓰기
- DSYNC/SYNC: 디스크 동기화 보장(성능 비용 발생)
- DELETE_ON_CLOSE: 스트림/채널을 닫을 때 파일 삭제(읽기에서도 사용 가능)
