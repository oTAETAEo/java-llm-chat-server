package com.example.aisocket.week2.benchmark;

public class ChatLockManager {

    public static synchronized String processAiQuery(String prompt) {
        System.out.println("[락 획득] " + Thread.currentThread().getName() + "이(가) 독점 연산을 시작합니다.");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[락 반납] " + Thread.currentThread().getName() + "이(가) 연산을 마치고 락을 놓습니다.");
        return "[AI Response] " + prompt + "에 대한 동기식 답변입니다.";
    }
}