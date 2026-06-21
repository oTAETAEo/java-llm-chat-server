package com.example.aisocket.ai;

public interface AiClient {

    String generateResponse(String prompt);

    String getClientName();
}
