## 표준 입출력(StdIn/StdOut) 가이드 — Java

이 문서는 콘솔 표준입력/출력 방식으로 문자열을 읽고 쓰는 방법을 정리합니다. 문제 1에서 요구하는 입력/출력 형식을 충족하도록 UTF-8과 한 줄 입출력을 기준으로 설명합니다.

### 한 줄 입력 후 한 줄 출력(기본형)
```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StdioExample {
  public static void main(String[] args) throws Exception {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
      String line = br.readLine();                 // 한 줄 입력(EOF이면 null)
      if (line == null) return;                    // 입력이 없으면 즉시 종료(대기 금지)
      if (line.isEmpty()) {                        // 빈 줄 입력은 빈 줄 출력으로 대응(형식 고정)
        System.out.println("");
        return;
      }
      // TODO: line을 처리
      System.out.println(line);                    // 결과 한 줄 출력
    }
  }
}
```

### 여러 줄 입력 처리(EOF까지)
```java
try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
  for (String line; (line = br.readLine()) != null; ) {
    if (line.isEmpty()) { System.out.println(""); continue; }
    // TODO: 처리 후
    System.out.println(line);
  }
}
```

### 종료되지 않고 계속 입력받는 대화형 루프(명시적 종료 명령)
```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReplExample {
  public static void main(String[] args) throws Exception {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
      // 안내 메시지(필수는 아님): 문제 채점 시 불필요한 출력이 요구되면 제거하세요.
      // System.out.println("type 'exit' to quit");
      while (true) {
        String line = br.readLine(); // Ctrl+Z(Windows)/Ctrl+D(macOS, Linux) 시 null 반환
        if (line == null) break;      // EOF로 종료
        String trimmed = line.trim();
        if (trimmed.equalsIgnoreCase("exit") || trimmed.equalsIgnoreCase("quit")) {
          break;                      // 명시적 종료 명령
        }
        if (trimmed.isEmpty()) {
          System.out.println("");    // 빈 줄은 빈 줄 출력 후 계속
          continue;
        }
        // TODO: 처리 로직(토큰화/사전 조회 등)
        System.out.println(trimmed);  // 결과 출력 후 루프 계속
      }
    }
  }
}
```

권장 종료 조건
- EOF(Ctrl+Z Enter / Ctrl+D) 또는 `exit`/`quit`와 같은 키워드로 종료
- 채점 환경에서 “추가 출력 금지” 조건이 있으면 안내 문구는 제거

### 입력 리다이렉션/파이프 예
- Windows CMD:
  - `echo Mary had a little lamb | java -cp out com.example.problem1.Problem1Main`
- macOS/Linux:
  - `echo 'Mary had a little lamb' | java -cp out com.example.problem1.Problem1Main`

### UTF-8 권장 이유
- OS/콘솔 기본 인코딩 차이를 제거하고, 한글·특수문자 깨짐을 방지합니다.
- 항상 `new InputStreamReader(System.in, StandardCharsets.UTF_8)`로 명시하세요.

### 문제 1과의 연결
- `Problem1Main`은 위 기본형을 사용하여 한 줄을 입력받고, 토큰화/사전조회 후 한 줄로 결과를 출력합니다.


