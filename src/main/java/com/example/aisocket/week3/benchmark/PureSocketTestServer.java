package com.example.aisocket.week3.benchmark;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PureSocketTestServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {

        // 기본 플랫폼 스레드 풀 방식 - 톰캣 모킹
//        ExecutorService executor = Executors.newFixedThreadPool(200);

        // Java 21 가상 스레드 방식
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Platform Thread vs Virtual Thread의 AI I/O 블로킹 효율 비교 서버 실행");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClientRequest(clientSocket));
            }

        } catch (Exception e) {
            System.err.println("예외 발생: " + e.getMessage());
        }
    }

    private static void handleClientRequest(Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {

            String requestLine = in.readLine();
            if (requestLine == null) return;

            // 블로킹
            Thread.sleep(1500);

            boolean isVirtual = Thread.currentThread().isVirtual();
            String threadInfo = Thread.currentThread().toString();

            String jsonResponseBody = String.format(
                    "{\"status\":\"SUCCESS\",\"isVirtual\":%b,\"currentThread\":\"%s\"}",
                    isVirtual, threadInfo
            );

            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: application/json\r\n");
            out.print("Content-Length: " + jsonResponseBody.getBytes().length + "\r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n");
            out.print(jsonResponseBody);
            out.flush();

        } catch (Exception e) {
        }
    }
}