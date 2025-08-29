package com.example.problem1.model;

/**
 * 임베딩 값을 보관하는 단순 래퍼.
 * - 문제 요구에 맞춰 "정수,정수,정수" 형태의 문자열을 그대로 유지한다.
 * - 숫자 파싱을 하지 않으므로 입력 형식을 변형하지 않고 보존한다.
 */
public final class EmbeddingVector {
    /** 예: "0,23,0" 같은 CSV 문자열 */
    private final String csv;

    /**
     * @param csv "정수,정수,정수" 형식의 문자열(검증은 로더 단계에서 수행)
     */
    public EmbeddingVector(String csv) {
        this.csv = csv;
    }

    /** 문제 명세에 맞춘 CSV 표현("x,y,z"). 그대로 반환한다. */
    public String toCsv() { return csv; }
}


