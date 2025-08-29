## 문제 1 — 아키텍처와 구현 코드(주석 포함)

### 목표
- **문장 입력(콘솔)** → 공백 기준 **토큰화** → **소문자 변환**으로 사전 조회 → 사전에 있으면 **임베딩 벡터 출력**, 없으면 **입력 단어 그대로** 출력 → 결과를 한 줄로 공백 구분하여 콘솔에 출력.
- 사전 파일은 실행 위치 기준 상대경로 `./DICTIONARY.TXT`.

### 처리 흐름
1. 한 줄 입력을 UTF-8로 읽음
2. 공백으로 토큰화(`split(" ")`) — 문제에서 공백 1개 기준 명시
3. 각 토큰에 대해 `toLowerCase(Locale.ROOT)`로 키 생성 후 사전 조회
4. 존재하면 `x,y,z` 형태의 문자열로 변환, 존재하지 않으면 원본 토큰 그대로 사용
5. 공백으로 조인하여 단 한 줄로 출력(불필요한 로그 금지)

### 데이터/파일 포맷
- `DICTIONARY.TXT` — `<단어>#<정수,정수,정수>` 행 형식, 단어는 **소문자**
- 예: `a#0,0,0`, `good#7,112,9816`

### 클래스 구성(문제 2·3 재사용 고려)
- `com.example.problem1.model.EmbeddingVector`
- `com.example.problem1.io.DictionaryLoader`
- `com.example.problem1.io.Dictionary`
- `com.example.problem1.nlp.Tokenizer`
- `com.example.problem1.Problem1Main`

### 클래스 상세와 코드 스니펫

#### 1) `EmbeddingVector` — 임베딩 데이터 모델
- "정수,정수,정수" 형태의 문자열을 그대로 보관하는 불변 객체
- 숫자 파싱을 하지 않고 입력 포맷을 보존하며 `toCsv()`로 그대로 반환

```java
package com.example.problem1.model;

public final class EmbeddingVector {
    private final String csv; // 예: "0,23,0"
    public EmbeddingVector(String csv) { this.csv = csv; }
    public String toCsv() { return csv; }
}
```

#### 2) `Dictionary` — 조회용 컨테이너
- 내부 `Map<String, EmbeddingVector>`를 캡슐화
- 소문자 단어 키로 조회

```java
package com.example.problem1.io;

import com.example.problem1.model.EmbeddingVector;
import java.util.*;

public final class Dictionary {
    private final Map<String, EmbeddingVector> wordToVector;
    private final java.util.Set<String> duplicateWords; // 로딩 시 발견된 중복 단어(보고용)

    public Dictionary(Map<String, EmbeddingVector> wordToVector, java.util.Set<String> duplicateWords) {
        this.wordToVector = Objects.requireNonNull(wordToVector);
        this.duplicateWords = duplicateWords == null ? java.util.Collections.emptySet()
                : java.util.Collections.unmodifiableSet(duplicateWords);
    }
    public EmbeddingVector get(String lowerCasedWord) { return wordToVector.get(lowerCasedWord); }
    public int size() { return wordToVector.size(); }
    public java.util.Set<String> getDuplicateWords() { return duplicateWords; }
}
```

#### 3) `DictionaryLoader` — 사전 파일 로더
- 형식: `<단어>#<정수,정수,정수>`
- 빈 줄/주석(`#...`) 무시, 형식 오류 라인은 무시
- 숫자 파싱을 하지 않고 CSV 문자열을 그대로 `EmbeddingVector`에 저장

```java
package com.example.problem1.io;

import com.example.problem1.model.EmbeddingVector;
import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*;
import java.util.*;

public final class DictionaryLoader {
    private DictionaryLoader() {}
    public static Dictionary load(Path path) throws IOException {
        if (!Files.exists(path)) throw new IOException("File not found at " + path.toAbsolutePath());
        Map<String, EmbeddingVector> map = new HashMap<>();
        Set<String> duplicates = new HashSet<>(); // 중복 단어 기록(보고용)
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line; while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] kv = line.split("#", 2); if (kv.length != 2) continue;
                if (!kv[1].contains(",")) continue; // 최소 형식 점검
                String word = kv[0];
                if (map.containsKey(word)) {
                    // [정책] 첫 등장 값 유지, 이후 동일 단어는 중복 목록으로만 수집
                    duplicates.add(word);
                } else {
                    map.put(word, new EmbeddingVector(kv[1]));
                }
            }
        }
        return new Dictionary(map, duplicates);
    }
}
```

#### 중복 처리 정책
- 현재 구현: "첫 등장 값을 유지"하고, 이후 동일 단어는 `duplicateWords`에만 기록
```java
if (map.containsKey(word)) {
    duplicates.add(word);        // 보고용 수집
} else {
    map.put(word, new EmbeddingVector(csv));
}
```

- 대안(수집하지 않는 케이스): 첫 등장 값만 사용하고 이후는 무시, 중복 목록도 만들지 않음
```java
map.putIfAbsent(word, new EmbeddingVector(csv));
```

#### 4) `Tokenizer` — 문제1 전용 토크나이저
- 공백 1칸 기준 분리(`split(" ")`)

```java
package com.example.problem1.nlp;
import java.util.*;

public final class Tokenizer {
    private Tokenizer() {}
    public static List<String> splitBySingleSpace(String line) {
        return Arrays.asList(line.split(" "));
    }
}
```

#### 5) `Problem1Main` — 실행 엔트리
- 콘솔 입력 → 토큰화 → 소문자 변환 후 사전 조회 → 결과 한 줄 출력
- 미등록 단어는 입력 원형을 그대로 출력

```java
package com.example.problem1;

import com.example.problem1.io.*;
import com.example.problem1.model.EmbeddingVector;
import com.example.problem1.nlp.Tokenizer;
import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*;
import java.util.*;

public final class Problem1Main {
    public static void main(String[] args) throws Exception {
        Dictionary dictionary = DictionaryLoader.load(Paths.get("DICTIONARY.TXT"));
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line = br.readLine(); if (line == null) return; if (line.isEmpty()) { System.out.println(""); return; }
            List<String> tokens = Tokenizer.splitBySingleSpace(line);
            List<String> outputs = new ArrayList<>(tokens.size());
            for (String token : tokens) {
                EmbeddingVector v = dictionary.get(token.toLowerCase(Locale.ROOT));
                outputs.add(v != null ? v.toCsv() : token);
            }
            System.out.println(String.join(" ", outputs));
        }
    }
}
```

### 경계 조건/결정 사항
- **미등록 단어**: 원본 토큰을 그대로 출력(대소문자/기호 포함)
- **사전 행 형식 오류**: 해당 행만 무시(로그 없음)
- **입력이 빈 줄**: 빈 줄 출력 후 종료
- **파일 인코딩**: UTF-8 고정(사전/콘솔 모두)

### 2·3번으로의 확장 포인트

### 빌드/실행 안내
- Windows 명령프롬프트 기준 컴파일/실행 명령은 `docs/p1-windows-cmd.md` 문서를 참고하세요.

- `Dictionary`, `DictionaryLoader`는 그대로 재사용 가능
- `EmbeddingVector`를 JSON 직렬화하면(문제2) 네트워크 전송/수신에 활용 가능
- 전처리 결과를 배열/리스트 형태로 유지해(문제3) 분류 결과와 쉽게 매핑 가능


