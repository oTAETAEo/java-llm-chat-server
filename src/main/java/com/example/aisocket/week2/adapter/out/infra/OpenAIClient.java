package com.example.aisocket.week2.adapter.out.infra;

import org.springframework.stereotype.Component;

@Component
public class OpenAIClient implements AiClient{

    @Override
    public String generateResponse(String prompt) throws InterruptedException {

//        Thread.sleep(10000);
        Thread.sleep(1000);

        return "[OpenAI GPT-4o] " + prompt + "에 대한 인공지능 분석 답변입니다.";
    }

    @Override
    public String getClientName() {
        return "External OpenAI API";
    }
}
