package com.example.problem1;

import com.example.problem1.io.Dictionary;
import com.example.problem1.io.DictionaryLoader;
import com.example.problem1.model.EmbeddingVector;
import com.example.problem1.nlp.Tokenizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 문제 1 실행 엔트리.
 * - 콘솔에서 한 줄 입력 → 토큰화 → 소문자 변환 후 사전 조회 → 결과 1줄 출력.
 * - 미등록 단어 정책: 입력한 단어 원형 그대로 출력.
 */
public final class Problem1Main {
    public static void main(String[] args) throws Exception {
        // 사전 로딩(상대경로). 실행 디렉터리에 DICTIONARY.TXT가 있어야 한다.
        Path dictPath = Paths.get("DICTIONARY.TXT");
        Dictionary dictionary = DictionaryLoader.load(dictPath);

        // 콘솔에서 UTF-8 한 줄 입력. 입력이 없으면 종료.
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line = br.readLine();
            if (line == null) return;
            if (line.isEmpty()) { System.out.println(""); return; }

            List<String> tokens = Tokenizer.splitBySingleSpace(line);
            List<String> outputs = new ArrayList<>(tokens.size());
            for (String token : tokens) {
                // 사전은 소문자 키로 보관. 입력 단어는 소문자로 변환하여 조회한다.
                // 미등록 단어 정책: 입력한 원형 그대로 결과에 포함.
                String key = token.toLowerCase(Locale.ROOT);
                EmbeddingVector v = dictionary.get(key);
                outputs.add(v != null ? v.toCsv() : token);
            }
            System.out.println(String.join(" ", outputs));
        }
    }
}


