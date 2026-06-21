package com.example.aisocket.ai;

import com.example.aisocket.ai.*;
import com.example.aisocket.tokenizer.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadChatServer {
    private static final int PORT = 8080;
    private static final int MAX_CONTEXT_TOKENS = 5000;          // 과거 대화 누적 한도 (테스트용)
    private static final int MAX_SINGLE_MESSAGE_TOKENS = 500;  // 단일 메시지 최대 허용 한도

    public static void main(String[] args) {
        AiClient currentClient = AiClientFactory.getClient(AiProvider.MOCK);
        Tokenizer tokenizer = new SimpleTokenizer();

        System.out.println("=== 싱글톤 팩토리 및 슬라이딩 윈도우 기반 AI 소켓 서버 구동 ===");
        System.out.println("초기 활성화된 AI 엔진: " + currentClient.getClientName());
        System.out.println("설정된 최대 대화 문맥 토큰 한도: " + MAX_CONTEXT_TOKENS);
        System.out.println("설정된 단일 메시지 최대 토큰 한도: " + MAX_SINGLE_MESSAGE_TOKENS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 포트 " + PORT + "에서 클라이언트 접속 대기 중...\n");

            while (true) {
                // 💡 수정 완료: 팩토리 메서드에 전체 문맥 제한(50)과 단일 메시지 제한(500)을 각각 주입합니다.
                TokenSlidingWindow slidingWindow = TokenSlidingWindow.create(MAX_CONTEXT_TOKENS, MAX_SINGLE_MESSAGE_TOKENS);

                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("[연결 수락] 클라이언트 진입: " + clientSocket.getRemoteSocketAddress());

                    out.println("AI 소켓 서버 연결 성공 (컨텍스트 한도: " + MAX_CONTEXT_TOKENS + " 토큰)");
                    out.println("명령어 안내: /switch OPENAI | /switch OLLAMA | /switch MOCK | exit");

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        inputLine = inputLine.trim();

                        if (inputLine.equalsIgnoreCase("exit") || inputLine.equalsIgnoreCase("quit")) {
                            out.println("서버와의 연결을 종료합니다.");
                            break;
                        }

                        if (inputLine.startsWith("/switch")) {
                            String[] tokens = inputLine.split(" ");
                            if (tokens.length < 2) {
                                out.println("[오류] /switch 뒤에 프로바이더를 입력하세요.");
                                continue;
                            }
                            try {
                                AiProvider targetProvider = AiProvider.valueOf(tokens[1].toUpperCase());
                                currentClient = AiClientFactory.getClient(targetProvider);

                                System.out.println("[엔진 교체] -> " + currentClient.getClientName());
                                out.println("[시스템] AI 엔진이 동적 변경되었습니다: " + currentClient.getClientName());
                            } catch (IllegalArgumentException e) {
                                out.println("[오류] 지원하지 않는 프로바이더 이름입니다.");
                            }
                            continue;
                        }

                        // 1. 유저 메시지 토큰 계산 및 슬라이딩 윈도우 적재
                        int userTokens = tokenizer.countTokens(inputLine);
                        ChatMessage userMessage = new ChatMessage(Role.USER, inputLine, userTokens);

                        int tokensBeforeAdd = slidingWindow.getCurrentTokens();

                        try {
                            // 단일 메시지 토큰 초과 검증 예외를 여기서 격리 트래핑합니다.
                            slidingWindow.addMessage(userMessage);

                            if (tokensBeforeAdd + userTokens > MAX_CONTEXT_TOKENS && slidingWindow.getCurrentTokens() < tokensBeforeAdd + userTokens) {
                                out.println("[알림] 시스템 자원 보호를 위해 컨텍스트 한도(" + MAX_CONTEXT_TOKENS + ")를 초과한 이전 대화가 메모리에서 삭제되었습니다.");
                            }
                        } catch (IllegalArgumentException e) {
                            // 예외 발생 시 로그 출력 후 클라이언트에 에러 전송 및 흐름 복구
                            System.err.println("[윈도우 적재 거부] " + e.getMessage());
                            out.println("[오류] 입력하신 메시지가 너무 길어 처리할 수 없습니다. 조금 더 짧게 입력해 주세요.");
                            continue;
                        }

                        System.out.println("[메시지 수신] \"" + inputLine + "\" | 유저 토큰: " + userTokens + " | 현재 윈도우 총합: " + slidingWindow.getCurrentTokens() + "/" + MAX_CONTEXT_TOKENS);

                        // 2. 전체 대화 문맥(프롬프트) 추출 및 외부 LLM 호출
                        String fullPrompt = slidingWindow.toPrompt();
                        System.out.println("--- [현재 엔진에 전달되는 누적 프롬프트 정보] ---\n" + fullPrompt + "---------------------------------------------");

                        String aiResponse = currentClient.generateResponse(fullPrompt);

                        // 3. AI 응답 메시지도 토큰 계산 후 슬라이딩 윈도우에 컨텍스트로 누적
                        int aiTokens = tokenizer.countTokens(aiResponse);
                        ChatMessage aiMessage = new ChatMessage(Role.ASSISTANT, aiResponse, aiTokens);

                        try {

                            slidingWindow.addMessage(aiMessage);
                        } catch (IllegalArgumentException e) {

                            System.err.println("[AI 응답 적재 거부] " + e.getMessage());
                        }

                        // 클라이언트로 최종 데이터 스트림 방출
                        out.println(aiResponse + " [현재 메모리 누적: " + slidingWindow.getCurrentTokens() + "/" + MAX_CONTEXT_TOKENS + " 토큰]");
                    }

                    System.out.println("[연결 해제] 클라이언트 통신 루프 마감.\n");
                } catch (Exception e) {

                    System.err.println("[통신 예외 발생] " + e.getMessage());
                }
            }
        } catch (Exception e) {

            System.err.println("[치명적 서버 에러] 서버 소켓 바인딩 실패: " + e.getMessage());
        }
    }
}