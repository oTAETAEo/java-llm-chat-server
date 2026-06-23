package com.example.aisocket.week1.ai;

public class MockAiClient implements AiClient {

    @Override
    public String generateResponse(String prompt) {
        return "[Mock AI] 테스트 답변: '" + prompt + "' 메시지를 성공적으로 수신했습니다.";
    }

    @Override
    public String getClientName() {
        return "로컬 Mock Engine (Test Mode)";
    }

}