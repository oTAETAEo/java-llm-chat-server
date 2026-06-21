package com.example.aisocket.benchmark.primitive;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClientPrimitiveDummy {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 9991;
    private static final int EXPECTED_BYTES = 100_000 * 768 * 8;

    public static void main(String[] args) throws IOException {
        byte[] trashBuffer = new byte[8192];
        System.out.println("[클라이언트] Primitive 서버(" + PORT + ")에 연결합니다...");

        try (Socket socket = new Socket(SERVER_IP, PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            long totalRead = 0;
            while (totalRead < EXPECTED_BYTES) {
                int read = in.read(trashBuffer);
                if (read == -1) break;
                totalRead += read;
            }
            System.out.println("[클라이언트] Primitive 데이터 수신 완료 (총 " + totalRead + " 바이트 소화)");
        }
    }
}