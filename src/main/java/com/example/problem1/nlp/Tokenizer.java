package com.example.problem1.nlp;

import java.util.Arrays;
import java.util.List;

/**
 * 문제1 스펙에 맞는 단순 토크나이저.
 * - 입력 문장을 공백 1칸 기준으로 분할한다.
 * - 이후 문제에서 요구가 확장되면(여러 공백, 구두점 처리 등) 이 클래스를 교체/확장한다.
 */
public final class Tokenizer {
    private Tokenizer() {}

    public static List<String> splitBySingleSpace(String line) {
        return Arrays.asList(line.split(" "));
    }
}


