package com.example.aisocket.week2.benchmark;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("[Thread-Per-Connection 서버] 포트 " + PORT + "에서 대기 중...");

            while (true) {

                Socket clientSocket = serverSocket.accept();

                Thread perConnectionThread = new Thread(new ClientHandler(clientSocket));
                perConnectionThread.setName("Thread-Per-Connection-Chat-" + perConnectionThread.getId());
                perConnectionThread.start();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}