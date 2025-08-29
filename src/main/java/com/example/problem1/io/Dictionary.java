package com.example.problem1.io;

import com.example.problem1.model.EmbeddingVector;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 사전 컨테이너.
 * - 내부 Map을 캡슐화하여 이후 정책 변경(UNK, 동의어, 캐시) 시 단일 진입점에서 제어한다.
 */
public final class Dictionary {
    private final Map<String, EmbeddingVector> wordToVector;
    private final Set<String> duplicateWords; // 로딩 중 발견된 중복 단어 목록(보고용)

    public Dictionary(Map<String, EmbeddingVector> wordToVector, Set<String> duplicateWords) {
        this.wordToVector = Objects.requireNonNull(wordToVector, "wordToVector");
        this.duplicateWords = duplicateWords == null ? Collections.emptySet() : Collections.unmodifiableSet(duplicateWords);
    }

    /** 소문자 단어 키로 임베딩 벡터를 조회한다. 존재하지 않으면 null. */
    public EmbeddingVector get(String lowerCasedWord) {
        return wordToVector.get(lowerCasedWord);
    }

    public int size() { return wordToVector.size(); }

    /** 중복으로 등장한 단어 목록(첫 등장 값 유지, 이후 등장 기록만 수집). */
    public Set<String> getDuplicateWords() { return duplicateWords; }
}


