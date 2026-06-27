package com.example.aisocket.week2.adapter.in.socket;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatConsoleClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)
        ) {

            System.out.println("[Connect] 서버 접속에 성공했습니다. (종료하려면 'exit' 입력)");

            Thread receiveThread = createReceiveThread(reader);

            while (true) {
                String input = scanner.nextLine();

                if (input == null || input.trim().isEmpty()) {
                    continue;
                }

                writer.println(input);

                if ("exit".equalsIgnoreCase(input.trim())) {
                    System.out.println("퇴장 명령을 전송했습니다. 클라이언트를 종료합니다.");
                    receiveThread.interrupt();
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("[Error] 클라이언트 구동 중 예외 발생: " + e.getMessage());
        }
    }

    private static @NonNull Thread createReceiveThread(BufferedReader reader) {
        Thread receiveThread = new Thread(() -> {
            try {
                String serverMessage;
                while (!Thread.currentThread().isInterrupted() && (serverMessage = reader.readLine()) != null) {
                    System.out.println("\n" + serverMessage);
                    System.out.print("입력: ");
                }
            } catch (Exception e) {
                System.out.println("\n📡 [Disconnect] 서버와의 수신 연결이 닫혔습니다.");
            }
        }, "Client-Receiver-Thread");

        receiveThread.start();
        return receiveThread;
    }
}