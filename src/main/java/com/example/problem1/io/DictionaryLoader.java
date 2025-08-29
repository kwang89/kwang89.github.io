package com.example.problem1.io;

import com.example.problem1.model.EmbeddingVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DICTIONARY.TXT 파일을 읽어 Dictionary를 생성한다.
 * - 행 형식: <단어>#<정수,정수,정수>
 * - 빈 줄과 '#'로 시작하는 주석은 무시한다.
 * - 잘못된 형식의 행은 조용히 건너뛴다(로그 금지, 실행 완결성 유지).
 */
public final class DictionaryLoader {
    private DictionaryLoader() {}

    public static Dictionary load(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("DICTIONARY.TXT not found at " + path.toAbsolutePath());
        }
        Map<String, EmbeddingVector> map = new HashMap<>();
        Set<String> duplicates = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] kv = line.split("#", 2);
                if (kv.length != 2) continue;
                String word = kv[0];
                // 숫자 파싱 없이 CSV 문자열을 그대로 보관한다.
                if (!kv[1].contains(",")) continue; // 최소 형식 점검
                if (map.containsKey(word)) {
                    // 첫 등장 값은 유지, 이후 동일 단어는 중복 목록에만 기록
                    duplicates.add(word);
                } else {
                    map.put(word, new EmbeddingVector(kv[1]));
                }
            }
        }
        return new Dictionary(map, duplicates);
    }
}


