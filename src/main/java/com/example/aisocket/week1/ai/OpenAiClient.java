package com.example.aisocket.week1.ai;

public class OpenAiClient implements AiClient {

    @Override
    public String generateResponse(String prompt) {
        // 추후 HttpClient를 통한 api.openai.com 통신 로직이 위치
        return "[OpenAI GPT-4o] " + prompt + "에 대한 인공지능 분석 답변입니다.";
    }

    @Override
    public String getClientName() {
        return "External OpenAI API";
    }
}
