package com.example.aisocket.ai;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("AI 소켓 클라이언트 구동 중...");

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("서버(" + SERVER_HOST + ":" + SERVER_PORT + ") 연결 성공\n");

            System.out.println("[서버 수신]: " + in.readLine());
            System.out.println("[서버 수신]: " + in.readLine());
            System.out.println("--------------------------------------------------");

            while (true) {
                System.out.print("프롬프트 입력: ");
                String userInput = scanner.nextLine();

                if (userInput == null || userInput.isBlank()) {
                    continue;
                }

                out.println(userInput);

                String serverResponse = in.readLine();
                System.out.println("[AI 응답]: " + serverResponse + "\n");

                if (userInput.trim().equalsIgnoreCase("exit") || userInput.trim().equalsIgnoreCase("quit")) {
                    System.out.println("클라이언트 프로그램을 종료합니다.");
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("[클라이언트 에러] 예외 발생: " + e.getMessage());
        }
    }
}