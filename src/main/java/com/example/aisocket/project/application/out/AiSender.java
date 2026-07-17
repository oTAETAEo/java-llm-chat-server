package com.example.aisocket.project.application.out;

import java.util.function.Consumer;

public interface AiSender {

    String execute(String promptText);

    void sendStream(String promptText, Consumer<String> chunkConsumer);

}
