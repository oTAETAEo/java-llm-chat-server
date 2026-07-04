package com.example.aisocket.week3.aichat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AiClient {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("자바 가상 스레드 AI 채팅 클라이언트");
        System.out.println("종료하려면 'exit' 또는 '종료'를 입력하세요.");
        System.out.println("==================================================");

        HttpClient client = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("\n나: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit") || userInput.equals("종료")) {
                System.out.println("AI 채팅 퇴장.");
                break;
            }

            if (userInput.isEmpty()) {
                continue;
            }

            String jsonPayload =
                    "{\"prompt\":\"" + JsonSupport.escape(userInput) + "\"}";

            HttpRequest request = createServerSendResuest(jsonPayload);

            System.out.print("AI 응답: ");
            printResponseMassage(client, request);
        }

        scanner.close();
    }

    private static void printResponseMassage(HttpClient client, HttpRequest request) {
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            try (InputStream in = response.body();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

                String serverLine;
                while ((serverLine = reader.readLine()) != null) {
                    if (serverLine.startsWith("data:")) {
                        String jsonChunk = serverLine.substring(5).trim();

                        if (jsonChunk.equals("[DONE]")) {
                            break;
                        }

                        String content = parseContentFromJsonChunk(jsonChunk);

                        System.out.print(content);
                        System.out.flush();
                    }
                }
            }
            System.out.println();

        } catch (Exception e) {
            System.err.println("\n[통신 에러] 서버가 켜져 있는지 확인하세요: " + e.getMessage());
        }
    }

    private static HttpRequest createServerSendResuest(String jsonPayload) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
    }

    private static String parseContentFromJsonChunk(String json) {

        try {
            if (json.contains("\"content\"")) {
                int keyIndex = json.indexOf("\"content\"");
                int startQuote = json.indexOf("\"", keyIndex + 9);
                int endQuote = json.indexOf("\"", startQuote + 1);

                String result = json.substring(startQuote + 1, endQuote);

                if (result.equals("\\n")) return "\n";
                if (result.equals("\\t")) return "\t";

                return result;
            }
        } catch (Exception ignored) {}

        return "";
    }
}
