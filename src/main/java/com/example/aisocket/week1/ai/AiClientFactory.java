package com.example.aisocket.week1.ai;

import java.util.EnumMap;
import java.util.Map;

public class AiClientFactory {

    private static final Map<AiProvider, AiClient> clients = new EnumMap<>(AiProvider.class);

    static {
        clients.put(AiProvider.OPENAI, new OpenAiClient());
        clients.put(AiProvider.MOCK, new MockAiClient());
    }

    public static AiClient getClient(AiProvider provider) {

        if (provider == null) {
            throw new IllegalArgumentException("AI 프로바이더 타입이 지정되지 않았습니다.");
        }

        AiClient client = clients.get(provider);

        if (client == null) {
            throw new IllegalArgumentException("지원하지 않는 프로바이더입니다: " + provider);
        }

        return client;
    }
}