package com.example.aisocket.week2.adapter.out.infra;

public interface AiClient {

    String generateResponse(String prompt);

    String getClientName();

}
