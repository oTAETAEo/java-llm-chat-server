package com.example.aisocket.week2.benchmark;

import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        System.out.println("[스레드 생성] " + Thread.currentThread().getName() + "이(가) 소켓을 전담 마크합니다.");

        try {

            ChatLockManager.processAiQuery("Connection Triggered");

        } catch (Exception e) {

            System.err.println("에러 발생: " + Thread.currentThread().getName() + " - " + e.getMessage());

        } finally {
            try {

                socket.close();
                System.out.println("[스레드 소멸] " + Thread.currentThread().getName() + "이(가) 퇴근합니다.");
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}