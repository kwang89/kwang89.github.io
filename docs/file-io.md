## 파일 입출력 (Java)

간단한 파일 읽기/쓰기 예제입니다.

```java
// 파일 쓰기
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class WriteExample {
  public static void main(String[] args) throws Exception {
    String content = "Hello, File!";
    Path path = Paths.get("example.txt");
    Files.write(path, content.getBytes(StandardCharsets.UTF_8));
  }
}
```

```java
// 파일 읽기
import java.nio.file.*;
import java.util.*;

public class ReadExample {
  public static void main(String[] args) throws Exception {
    Path path = Paths.get("example.txt");
    List<String> lines = Files.readAllLines(path);
    lines.forEach(System.out::println);
  }
}
```


